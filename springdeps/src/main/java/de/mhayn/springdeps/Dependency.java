package de.mhayn.springdeps;

/**
 * Created by User on 26.03.2017.
 */
public class Dependency implements Comparable<Dependency> {
    private String groupId;
    private String artifactId;
    private String version;
    private boolean optional;
    private String scope;

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

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    @Override
    public String toString() {
        return "Dependency{" +
                "groupId='" + groupId + '\'' +
                ", artifactId='" + artifactId + '\'' +
                ", version='" + version + '\'' +
                ", optional=" + optional +
                ", scope='" + scope + '\'' +
                '}';
    }

    public String versionString() {
        return "<" + artifactId + ".version>" + version + "</" + artifactId + ".version>";
    }

    private String element(String name, String content) {
        if (content == null) {
            return "";
        }
        return "<" + name + ">" + content + "</" + name + ">";
    }

    public String dependencyString() {
        StringBuilder sb = new StringBuilder();
        sb.append("<dependency>\r\n");
        sb.append("\t").append(element("groupId", groupId)).append("\r\n");
        sb.append("\t").append(element("artifactId", artifactId)).append("\r\n");
        String vs = "${" + artifactId  + ".version" + "}";
        sb.append("\t").append(element("version", vs)).append("\r\n");
        if (scope != null)
            sb.append("\t").append(element("scope", scope)).append("\r\n");
        if (optional)
            sb.append("\t").append(element("optional", Boolean.toString(optional))).append("\r\n");
        sb.append("</dependency>\r\n");
        return sb.toString();
    }

    @Override
    public int compareTo(Dependency o) {
        int res = this.groupId.compareTo(o.groupId);
        if (res != 0) {
            res = this.artifactId.compareTo(o.artifactId);
        }
        return res;
    }



}
