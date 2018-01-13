import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.xml.sax.SAXException;

import javax.naming.ldap.LdapName;
import javax.xml.parsers.ParserConfigurationException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Date;

/**
 * Created by Umar on 13/10/2016.
 */
public class Indexer {
    Indexer() {
    }

    public static void main(String[] args) {
        String usage =
                "java org.apache.lucene.demo.IndexFiles [-index INDEX_PATH] [-docs DOCS_PATH] [-update]\n\nThis indexes the documents in DOCS_PATH, creating a Lucene indexin INDEX_PATH that can be searched with SearchFiles";
        String indexPath = "Index";
        String docsPath = "Data";

        boolean create = false;

        for (int i = 0; i < args.length; i++) {
            if ("-index".equals(args[i])) {
                indexPath = args[i + 1];
                i++;
            } else if ("-docs".equals(args[i])) {
                docsPath = args[i + 1];
                i++;
            } else if ("-update".equals(args[i])) {
                create = false;
            }
        }

        if (docsPath == null) {
            System.err.println("Usage: " + usage);
            System.exit(1);
        }

        Path docPath = Paths.get(docsPath);
        if (!Files.isReadable(docPath)) {
            System.out.println("Document directory \'" + docPath.toAbsolutePath() + "\' does not exist or is not readable, please check the path");
            System.exit(1);
        }

        Date start = new Date();

        try {
            System.out.println("Indexing to directory \'" + indexPath + "\'...");
            Directory d= FSDirectory.open(Paths.get(indexPath));
            StandardAnalyzer analyzer = new StandardAnalyzer();
            IndexWriterConfig iwc = new IndexWriterConfig(analyzer);

            if (create) {
                iwc.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
            } else {
                iwc.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);
            }

            IndexWriter writer = new IndexWriter(d, iwc);
            indexDocs(writer, docPath);

            writer.close();

            Date end = new Date();
            System.out.println(end.getTime() - start.getTime() + " total milliseconds");

        } catch (IOException e) {
            System.out.println(" caught a " + e.getClass() + "\n with message: " + e.getMessage());
        }

    }

    private static void indexDocs(final IndexWriter writer, Path path) throws IOException {
        if (Files.isDirectory(path)) {
            Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    try {
                        indexDoc(writer, file, attrs.lastModifiedTime().toMillis());
                    } catch (IOException e) {

                    }

                    return FileVisitResult.CONTINUE;
                }
            });
        } else {
            indexDoc(writer, path, Files.getLastModifiedTime(path).toMillis());
        }

    }

    private static void addToDoc(Path file, long lastModified, Document doc, InputStream stream) throws IOException, SAXException, ParserConfigurationException {
        StringField pathField = new StringField("path", file.toString(), Field.Store.YES);
        doc.add(pathField);
        doc.add(new LongPoint("modified", lastModified));
        doc.add(new TextField("contents", new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))));
        doc.add(new TextField("filename", file.toFile().getName(), Field.Store.YES));
        System.out.println(file.getFileName());
        XMLParser.parse(file.toFile());
        doc.add(new TextField("title", XMLParser.title, Field.Store.YES));
        doc.add(new TextField("persona", XMLParser.persona.toString(), Field.Store.YES));
        doc.add(new TextField("sceneDesc", XMLParser.sceneDesc, Field.Store.YES));
        doc.add(new TextField("speaker", XMLParser.speaker.toString(), Field.Store.YES));
        doc.add(new TextField("stageDir", XMLParser.stageDir.toString(), Field.Store.YES));
        doc.add(new TextField("line", XMLParser.line.toString(), Field.Store.YES));
        doc.add(new TextField("subtitle", XMLParser.subtitle, Field.Store.YES));
    }

    private static void indexDoc(IndexWriter writer, Path file, long lastModified) throws IOException {
        InputStream stream = Files.newInputStream(file);
        Throwable var5 = null;

        try {
            Document doc = new Document();
            addToDoc(file, lastModified, doc, stream);

            if (writer.getConfig().getOpenMode() == IndexWriterConfig.OpenMode.CREATE) {
                System.out.println("adding " + file);
                writer.addDocument(doc);
            } else {
                System.out.println("updating " + file);
                writer.updateDocument(new Term("path", file.toString()), doc);
            }
        } catch (Throwable var15) {
            var5 = var15;
            try {
                throw var15;
            } catch (ParserConfigurationException | SAXException e) {
                e.printStackTrace();
            }
        } finally {
            if (var5 != null) {
                try {
                    stream.close();
                } catch (Throwable var14) {
                    var5.addSuppressed(var14);
                }
            } else {
                stream.close();
            }

        }

    }
}
