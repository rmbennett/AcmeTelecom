package com.acmetelecom;

class HtmlPrinter implements Printer {

    private static Printer instance = new HtmlPrinter();

    private HtmlPrinter() {
    }

    public static Printer getInstance() {
        return instance;
    }

    public String printHeading(String name, String phoneNumber, String pricePlan) {
        StringBuilder output = new StringBuilder(beginHtml());
        output.append(h2(name + "/" + phoneNumber + " - " + "Price Plan: " + pricePlan));
        output.append(beginTable());
        return output.toString();
    }

    private String beginTable() {
        StringBuilder output = new StringBuilder("<table border=\"1\">");
        output.append(tr(th("Time") + th("Number") + th("Duration") + th("Cost")));
        return output.toString();
    }

    private String endTable() {
        return "</table>";
    }

    private String h2(String text) {
        return "<h2>" + text + "</h2>";
    }

    public String printItem(String time, String callee, String duration, String cost) {
        return tr(td(time) + td(callee) + td(duration) + td(cost));
    }

    private String tr(String text) {
        return "<tr>" + text + "</tr>";
    }

    private String th(String text) {
        return "<th width=\"160\">" + text + "</th>";
    }

    private String td(String text) {
        return "<td>" + text + "</td>";
    }

    public String printTotal(String total) {
        StringBuilder output = new StringBuilder(endTable());
        output.append(h2("Total: " + total));
        output.append(endHtml());
        return output.toString();
    }

    private String beginHtml() {
        StringBuilder output = new StringBuilder();
        output.append("<html>");
        output.append("<head></head>");
        output.append("<body>");
        output.append("<h1>");
        output.append("Acme Telecom");
        output.append("</h1>");
        return output.toString();
    }

    private String endHtml() {
        StringBuilder output = new StringBuilder();
        output.append("</body>");
        output.append("</html>");
        return output.toString();
    }
}
