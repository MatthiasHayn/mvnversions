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
import java.util.List;


/**
 * Created by User on 26.03.2017.
 */
public class Pom extends DefaultHandler {
    private enum ParseContext {pc_none, pc_parent, pc_dependency};
    private int elementDepth = 0;

    private final String fileName;
    private String source;

    private List<Dependency> dependencies = new ArrayList<>();
    private Pom parent;
    private List<Pom> siblings = new ArrayList<>();
    private List<Pom> children = new ArrayList<>();

    private StringBuilder sb = new StringBuilder();
    private Dependency dep;

    private String groupId;
    private String artifactId;
    private String parentGroupId;
    private String parentArtifactId;
    private ParseContext parseContext = ParseContext.pc_none;


    public Pom(String fileName) {
        this.fileName = fileName;
        try {
            scanPom();
            if (groupId == null) {
                groupId = parentGroupId;
            }
            System.out.println(fileName);
            System.out.println("Name: " + nameAsKey());
            System.out.println("Parent: " + parentAsKey());

            System.out.println(dependencies);
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addChild(Pom child) {
        child.parent = this;
        for (Pom sibling:children) {
            sibling.siblings.add(child);
            child.siblings.add(sibling);
        }
        children.add(child);

    }

    public String nameAsKey() {
        return groupId + "_" + artifactId;
    }

    public String parentAsKey() {
        return parentGroupId + "_" +parentArtifactId;
    }

    public Pom getParent() {
        return parent;
    }

    public List<Pom> getChildren() {
        return children;
    }

    public List<Pom> getSiblings() {
        return siblings;
    }

    private void scanPom() throws ParserConfigurationException, SAXException, IOException {
        SAXParserFactory factory = SAXParserFactory.newInstance();
        SAXParser saxParser = factory.newSAXParser();
        saxParser.parse(new File(fileName), this);
    }

    @Override
    public void startElement(String namespaceURI, String localName,
                             String qName, Attributes atts) {
        elementDepth++;
        sb = new StringBuilder();
        if (qName.equals("dependency")) {
            parseContext = ParseContext.pc_dependency;
            dep = new Dependency();
        } else if (qName.equals("parent")) {
            parseContext = ParseContext.pc_parent;
        }
    }

    @Override
    public void endElement(String namespaceURI, String localName, String qName) {

        if (parseContext.equals(ParseContext.pc_none) && (elementDepth == 2)) {
            if (qName.equals("artifactId")) {
                artifactId = sb.toString();
            } else if (qName.equals("groupId")) {
                groupId = sb.toString();
            }
        }
        else if (parseContext.equals(ParseContext.pc_parent)) {
            if (qName.equals("artifactId")) {
                parentArtifactId = sb.toString();
            } else if (qName.equals("groupId")) {
                parentGroupId = sb.toString();
            }
        }
        else if (parseContext.equals(ParseContext.pc_dependency)) {
            if (qName.equals("artifactId")) {
                dep.setArtifactId(sb.toString());
            } else if (qName.equals("groupId")) {
                dep.setGroupId(sb.toString());
            } else if (qName.equals("version")) {
                dep.setVersion(sb.toString());
            } else if (qName.equals("optional")) {
                dep.setOptional(Boolean.parseBoolean(sb.toString()));
            } else if (qName.equals("scope")) {
                dep.setScope(sb.toString());
            }
        }

        if (qName.equals("dependency")) {
            parseContext = ParseContext.pc_none;
            dependencies.add(dep);
        }
        else if (qName.equals("parent")) {
            parseContext = ParseContext.pc_none;
        }
        elementDepth--;
    }

    @Override
    public void characters(char[] ch, int start, int length) {
        sb.append(ch, start, length);
    }

}
