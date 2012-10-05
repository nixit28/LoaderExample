package com.nix.loadeer;

/**
 * Created with IntelliJ IDEA.
 * User: Nixit
 * Date: 10/4/12
 * Time: 2:45 PM
 */
public class SearchResult {

    private String symbole;
    private String name;
    private String exch;
    private String type;
    private String exchDisp;
    private String typeDisp;

    public String getSymbole() {
        return symbole;
    }

    public void setSymbole(String symbole) {
        this.symbole = symbole;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getExch() {
        return exch;
    }

    public void setExch(String exch) {
        this.exch = exch;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getExchDisp() {
        return exchDisp;
    }

    public void setExchDisp(String exchDisp) {
        this.exchDisp = exchDisp;
    }

    public String getTypeDisp() {
        return typeDisp;
    }

    public void setTypeDisp(String typeDisp) {
        this.typeDisp = typeDisp;
    }

    @Override
    public String toString() {

        return getName();
    }
}
