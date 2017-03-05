package de.mhayn.springdeps;


import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class App {
    private static final String pomSource = "spring-2.5.6.SEC03.pom";

    private List<Dependency> dependencyList = new ArrayList<>();

    class Dependency implements Comparable<Dependency>{
        private String groupId;
        private String artifactId;
        private String version;
        private boolean optional;

        public String getGroupId() {
            return groupId;
        }

        public void setGroupId(String groupId) {
            this.groupId = groupId;
        }

        public String getArtifactId() {
            return artifactId;
        }

        public void setArtifactId(String artifactId) {
            this.artifactId = artifactId;
        }

        public String getVersion() {
            return version;
        }

        public void setVersion(String version) {
            this.version = version;
        }

        public boolean isOptional() {
            return optional;
        }

        public void setOptional(boolean optional) {
            this.optional = optional;
        }

        @Override
        public String toString() {
            return "Dependency{" +
                    "groupId='" + groupId + '\'' +
                    ", artifactId='" + artifactId + '\'' +
                    ", version='" + version + '\'' +
                    ", optional=" + optional +
                    '}';
        }

        public String versionString() {
            return "<" + artifactId + ".version>" + version + "</" + artifactId + ".version>";
        }

        @Override
        public int compareTo(Dependency o) {
            return this.artifactId.compareTo(o.artifactId);
        }
    }


    class PomHandler extends DefaultHandler {
        private StringBuilder sb = new StringBuilder();
        private Dependency dep;
        private boolean inDep = false;

        @Override
        public void startElement(String namespaceURI, String localName,
                                 String qName, Attributes atts) {
            sb = new StringBuilder();
            if (qName.equals("dependency")) {
                inDep = true;
                dep = new Dependency();
            }
        }

        @Override
        public void endElement(String namespaceURI, String localName, String qName) {
            if (qName.equals("dependency")) {
                inDep = false;
                dependencyList.add(dep);
            }
            ;
            if (!inDep)
                return;
            if (qName.equals("artifactId")) {
                dep.setArtifactId(sb.toString());
            } else if (qName.equals("groupId")) {
                dep.setGroupId(sb.toString());
            } else if (qName.equals("version")) {
                dep.setVersion(sb.toString());
            } else if (qName.equals("optional")) {
                dep.setOptional(Boolean.parseBoolean(sb.toString()));
            }
        }

        @Override
        public void characters(char[] ch, int start, int length) {
            sb.append(ch, start, length);
        }


    }

    private void scanDependenciesAndCreateVersionTags() throws ParserConfigurationException, SAXException, IOException {
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource(pomSource).getFile());
        SAXParserFactory factory = SAXParserFactory.newInstance();
        SAXParser saxParser = factory.newSAXParser();
        DefaultHandler handler = new PomHandler();
        saxParser.parse(file, handler);
        Collections.sort(dependencyList);
        for (Dependency dep:dependencyList) {
            System.out.println(dep.versionString());
        }
    }

    public static void main(String[] args) throws IOException, SAXException, ParserConfigurationException {
        App app = new App();
        app.scanDependenciesAndCreateVersionTags();

    }
}
