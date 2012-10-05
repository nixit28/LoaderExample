package com.nix.loadeer;

/**
 * Created with IntelliJ IDEA.
 * User: Nixit
 * Date: 10/4/12
 * Time: 3:25 PM
 */
public interface ApiConstant {

    String REPLACER = "%@";
    String UNWANTED = "YAHOO.Finance.SymbolSuggest.ssCallback(";

    String SEARCH_API = "http://d.yimg.com/autoc.finance.yahoo.com/autoc?query=" + REPLACER + "&callback=YAHOO.Finance.SymbolSuggest.ssCallback";


}
