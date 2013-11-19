package com.acmetelecom;

class HtmlPrinter implements Printer {

    private static Printer instance = new HtmlPrinter();

    private HtmlPrinter() {
    }

    public static Printer getInstance() {
        return instance;
    }

    public String heading(String name, String phoneNumber, String pricePlan) {
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
        return "</table>\n";
    }

    private String h2(String text) {
        return "<h2>" + text + "</h2>\n";
    }

    public String item(String time, String callee, String duration, String cost) {
        return tr(td(time) + td(callee) + td(duration) + td(cost));
    }

    private String tr(String text) {
        return "<tr>" + text + "</tr>\n";
    }

    private String th(String text) {
        return "<th width=\"160\">" + text + "</th>";
    }

    private String td(String text) {
        return "<td>" + text + "</td>";
    }

    public String total(String total) {
        StringBuilder output = new StringBuilder(endTable());
        output.append(h2("Total: " + total));
        output.append(endHtml());
        return output.toString();
    }

    private String beginHtml() {
        StringBuilder output = new StringBuilder();
        output.append("<html>\n");
        output.append("<head></head>\n");
        output.append("<body>\n");
        output.append("<h1>\n");
        output.append("Acme Telecom\n");
        output.append("</h1>\n");
        return output.toString();
    }

    private String endHtml() {
        StringBuilder output = new StringBuilder();
        output.append("</body>\n");
        output.append("</html>\n");
        return output.toString();
    }
}
