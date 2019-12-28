public class Paragraph implements Line{
    public String content;

    public Paragraph(String content) {
        this.content = content;
    }

    @Override
    public String out() {
        return content;
    }
}
