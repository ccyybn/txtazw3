import java.util.List;
import java.util.stream.Collectors;

public class TitleTree {
    public Title title;
    public List<TitleTree> subTitles;

    private TitleTree() {
    }

    private TitleTree(Title title) {
        this.title = title;
    }

    public void println() {
        println(false, -1);
    }

    private void println(boolean isError, int level) {
        boolean isRoot = title == null;
        if (!isRoot) {

            for (int i = 0; i < level; i++) {
                System.out.print("    ");
            }
            if (isError) {
                System.out.print("ERROR→→→→→→→→→→→→→→→→→→→→→");
            }
            System.out.println(title.content);
        }
        int index = 1;
        for (TitleTree s : subTitles) {
            if (s.title.titleIndex != index) {
                s.println(true, level + 1);
            } else {
                s.println(false, level + 1);
            }
            index = s.title.titleIndex + 1;
        }
    }

    public static TitleTree parseTree(List<Title> titles) {
        TitleTree root = new TitleTree();
        List<TitleTree> titleTrees = titles.stream().filter(t -> t.parentId == 0).map(TitleTree::new).collect(Collectors.toList());
        for (TitleTree tree : titleTrees) {
            parseTree(tree, titles);
        }
        root.subTitles = titleTrees;
        return root;
    }

    private static void parseTree(TitleTree titleTree, List<Title> titles) {
        titleTree.subTitles = titles.stream().filter(t -> t.parentId == titleTree.title.id).map(TitleTree::new).collect(Collectors.toList());
        for (TitleTree subTitle : titleTree.subTitles) {
            parseTree(subTitle, titles);
        }
    }

    @Override
    public String toString() {
        if (title == null) {
            return "root";
        } else {
            return title.toString();
        }
    }
}
