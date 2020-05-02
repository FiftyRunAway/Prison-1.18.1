package org.runaway.menu.design;

public class Row {

    private RowType type = RowType.NUMBER;

    /*
    if type == Number row should be defined!
    */

    private int row = 0;

    public Row(RowType rowType){
        this.type = rowType;
    }

    public Row(RowType rowType, Integer row){
        this.type = rowType;
        this.row = row;
    }

    public int getRow() {
        return row;
    }

    public RowType getType() {
        return type;
    }

}
