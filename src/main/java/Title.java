public class Title implements Line {
    public int id;
    public int level;
    public String titleName;
    public int titleIndex;
    public String content;
    public int parentId;

    public Title(int id, int level, String titleName, int titleIndex, String content) {
        this.id = id;
        this.level = level;
        this.titleName = titleName;
        this.titleIndex = titleIndex;
        this.content = content;
    }

    @Override
    public String out() {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < level + 1; i++) {
            builder.append("#");
        }
        builder.append(" ");
        builder.append(content);
        return builder.toString();
    }

    @Override
    public String toString() {
        return content;
    }
}
