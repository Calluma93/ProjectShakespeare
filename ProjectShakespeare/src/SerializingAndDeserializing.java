import com.sun.xml.internal.ws.wsdl.writer.document.http.Address;

import java.io.*;

public class SerializingAndDeserializing {


    public void serializeSearches(RecentSearches rs) {

        try (ObjectOutputStream oos =
                     new ObjectOutputStream(new FileOutputStream("c:\\temp\\address.ser"))) {

            oos.writeObject(rs);
            System.out.println("Done");

        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    public RecentSearches deserialzeSearches() {
        String filename = "c:\\temp\\address.ser";
        RecentSearches rs = null;

        try (ObjectInputStream ois
                     = new ObjectInputStream(new FileInputStream(filename))) {

            rs = (RecentSearches) ois.readObject();

        } catch (Exception ex) {
            //ex.printStackTrace();
            System.out.println("Okay, no recent searches");
        }

        return rs;

    }

}