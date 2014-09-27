package cmput301.cauni.easydo.view.enums;

public enum TodoFiles
{
    TODO("ActiveTodo"),
    ARCHIVEDTODO("ArchivedTodo");

    private String archiveName;
    TodoFiles(String value)
    {
        this.archiveName = value;
    }

    public String getArchiveName()
    {
        return this.archiveName;
    }
}
