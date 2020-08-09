package Engine;
import jaxb.generatedClasses.SuperDuperMarketDescriptor;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.concurrent.ConcurrentMap;

public class SuperMarketLogic {
    private SuperDuperMarketDescriptor sdm;

    public String loadData(String filePath, Boolean dataLoaded) {
        try {
            if(filePath.isEmpty()) {
                return "<The file path that was given was empty>";
            }

            if(filePath.length()>= 4 && filePath.substring(filePath.length()-3).toLowerCase().equals("xml"))
            {
                File file = new File(filePath);
                if(file.exists())
                {
                    //sdm = loadXML(file);
                }
                else {
                    return "<The file does not exist in the path that was given>";
                }
            }
            else
            {
                return "<The file type that was given is not .xml>";
            }
        }
        catch (SecurityException e){
            throw new SecurityException("<The file access was blocked by the file's security manager>");
        }
        catch (NullPointerException e){
            throw new NullPointerException("<The path that was given is NULL>");
        }

        return "sth";
    }

    private SuperDuperMarketDescriptor loadXML(File file) throws JAXBException {
        try {
            SuperDuperMarketDescriptor temp;
            JAXBContext jaxbContext = JAXBContext.newInstance(SuperDuperMarketDescriptor.class);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            temp = (SuperDuperMarketDescriptor)jaxbUnmarshaller.unmarshal(file);
            //need to validate temp fields in an external method.

        }
        catch (JAXBException e) {
            throw new JAXBException(e.getMessage());
        }

        return null;
    }
}
