package de.mhayn.springdeps;

import org.xml.sax.SAXException;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.*;

public class App {
    private static final String pomSourceFolder = "d:\\daten\\develop\\pdfbox\\pdfbox-trunk";


    private List<Pom> poms = new ArrayList<>();
    private Map<String, Pom> pomsByName = new HashMap<>();


    private void buildTree() {
        for (Pom pom:poms) {
            Pom parent = pomsByName.get(pom.parentAsKey());
            if (parent != null) {
                parent.addChild(pom);
            }
            else {
                System.out.println(pom.parentAsKey() + " not found for: " + pom.nameAsKey() );
            }

        }

        for (Pom pom:poms) {
            if (pom.getParent() == null) {
                System.out.println("No parent: " + pom.nameAsKey());
                System.out.println("Has " + pom.getChildren().size() + " children");
                Pom child1 = pom.getChildren().get(0);
                System.out.println("Pom " + child1.nameAsKey() + " has " + child1.getSiblings().size() + " siblings and " + child1.getChildren().size() + " children");
            }
        }
    }

    private void scanPoms(String startFolderName) {
        if (startFolderName == null) {
            startFolderName = pomSourceFolder;
        }
        File startFolder = new File(startFolderName);
        for (File f:startFolder.listFiles()) {
            if (f.isDirectory()) {
                scanPoms(f.getAbsolutePath());
            }
            else {
                if (f.getName().toLowerCase().equals("pom.xml")) {
                    Pom pom = new Pom(f.getAbsolutePath());
                    poms.add(pom);
                    pomsByName.put(pom.nameAsKey(), pom);
                }
            }
        }

    }

    public static void main(String[] args) throws IOException, SAXException, ParserConfigurationException {
        App app = new App();

        app.scanPoms(null);
        app.buildTree();

    }
}
