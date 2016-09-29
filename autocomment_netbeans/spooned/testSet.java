

// default package (CtPackage.TOP_LEVEL_PACKAGE_NAME in Spoon= unnamed package)



public class testSet {
    private int count;

    private double totalGrade;

    private List<String> list;

    public void setCount(int count) {
        testSet.this.count = count;
    }

    public void setTotalGrade(double newgrade) {
        totalGrade = newgrade;
    }

    public void setList(List<String> list) {
        testSet.this.list = list;
    }

    public void setAasd() {
        (count)++;
    }

    public void setASDdasd(int asd) {
        count = asd++;
    }

    public void setASDdasda(int asd) {
        count = asd / (totalGrade);
    }

    public void setASDdasdc(int asd) {
        count = asd++;
        count = totalGrade;
    }
}

