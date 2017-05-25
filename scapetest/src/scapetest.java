import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;

/**
 * Created by Kin To Pang on 15/06/14.
 * HTML Scraping done by JSoup - http://jsoup.org/
 */
public class scapetest {

    private static final String host = "http://www.heartofthecards.com";
    private static Document doc;

    public static void main(String[] args) throws IOException {

        Scanner scanner = new Scanner(System.in);
        int choice;
        do {
            System.out.println("ZweiSteele V2.0 Test Harness!");
            System.out.println("1) Download Series");
            System.out.println("2) Download Series Card List");
            System.out.println("3) Download All Series Card Lists");
            System.out.println("4) Download Single Card");
            System.out.println("5) Download All Cards of a Series");
            System.out.println("6) Check For Updates");
            System.out.println("7) Exit");
            System.out.print("Choice? ");
            choice = scanner.nextInt();

            switch (choice) {
                case 1:
                    downloadSeries();
                    break;
                case 2:
                    /*BufferedReader br = new BufferedReader(new FileReader("trialdecks.txt"));
                    String line;
                    while ((line = br.readLine()) != null) {
                        System.out.println(line);
                    }*/
                    downloadSingleCardList("trialdecks", "Disgaea", "/code/cardlist.html?pagetype=ws&cardset=S02-ws2008std");
                    break;
                case 3:
                    downloadAllCardLists();
                    break;
                case 4:
                    //downloadSingleCard("");
                    break;
                case 5:
                    downloadAllCardsOfASeries();
                    break;
                case 6:
                    checkForUpdate();
                    break;
                default:
                    choice = 7;
                    break;
            }
        } while (choice != 7);

    }

    private static void downloadSeries() throws IOException {

        Map<String, String> tdSeries = new HashMap<>();
        Map<String, String> boosterSeries = new HashMap<>();
        Map<String, String> extraSeries = new HashMap<>();
        Map<String, String> miscSeries = new HashMap<>();

        Elements seriesRaw;
        //connect and parse webpage
        doc = Jsoup.connect(host + "/code/cardlist.html?pagetype=ws").get();

        HashMap<String, String> types = new HashMap<>();

        types.put("td", "table[width=100%][style=font-size:14px]>tbody>tr:first-of-type>td:first-of-type>a");
        types.put("booster", "table[width=100%][style=font-size:14px]>tbody>tr:first-of-type>td:nth-of-type(3)>a");
        types.put("extra", "table[width=100%][style=font-size:14px]>tbody>tr:nth-of-type(3)>td:first-of-type>a");
        types.put("misc", "table[width=100%][style=font-size:14px]>tbody>tr:nth-of-type(3)>td:nth-of-type(3)>a");

        //selects the needed elements from the parsed webpage by CSS selectors
        for (HashMap.Entry<String, String> entry : types.entrySet()) {
            seriesRaw = doc.select(entry.getValue());

            switch (entry.getKey()) {
                case "td":
                    for (Element seriesLinks : seriesRaw) {
                        //Put found elements into map
                        tdSeries.put(seriesLinks.text(), seriesLinks.attr("href"));
                    }
                    break;
                case "booster":
                    for (Element seriesLinks : seriesRaw) {
                        //Put found elements into map
                        boosterSeries.put(seriesLinks.text(), seriesLinks.attr("href"));
                    }
                    break;
                case "extra":
                    for (Element seriesLinks : seriesRaw) {
                        //Put found elements into map
                        extraSeries.put(seriesLinks.text(), seriesLinks.attr("href"));
                    }
                    break;
                case "misc":
                    for (Element seriesLinks : seriesRaw) {
                        //Put found elements into map
                        miscSeries.put(seriesLinks.text(), seriesLinks.attr("href"));
                    }
                    break;
                default:
                    break;
            }
        }

        PrintWriter writer = new PrintWriter("trialdecks.txt", "UTF-8");
        for (HashMap.Entry<String, String> entry : tdSeries.entrySet()) {
            writer.println(entry.getKey() + "|" + entry.getValue());
        }
        System.out.println(tdSeries.size() + " Trial Decks Downloaded");
        writer.close();

        writer = new PrintWriter("boosters.txt", "UTF-8");
        for (HashMap.Entry<String, String> entry : boosterSeries.entrySet()) {
            writer.println(entry.getKey() + "|" + entry.getValue());
        }
        System.out.println(boosterSeries.size() + " Boosters Downloaded");
        writer.close();

        writer = new PrintWriter("extra.txt", "UTF-8");
        for (HashMap.Entry<String, String> entry : extraSeries.entrySet()) {
            writer.println(entry.getKey() + "|" + entry.getValue());
        }
        System.out.println(extraSeries.size() + " Extra Packs Downloaded");
        writer.close();

        writer = new PrintWriter("misc.txt", "UTF-8");
        for (HashMap.Entry<String, String> entry : miscSeries.entrySet()) {
            writer.println(entry.getKey() + "|" + entry.getValue());
        }
        System.out.println(miscSeries.size() + " Misc Downloaded");
        writer.close();
    }

    //List format map<Card ID,  Array[carddata string,link]>
    //card data string format: english card name/japanese card name/card type/card color in hex
    private static void downloadSingleCardList(String type, String name, String link) {
        Map<String, String> cardList = new TreeMap<>();

        Elements cardListRaw;
        try {
            doc = Jsoup.connect(host + link).get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        cardListRaw = doc.select(".cardlist > tbody > tr");
        String spCheckerString = "";
        int i = 1;
        if (type.equals("trialdecks") && name.equals("Disgaea")) {
            i = 21;
        }
        while (i < cardListRaw.size()) {
            spCheckerString = cardListRaw.get(i).child(0).text().split("-")[1].substring(3);
            //index:0 = id+url, 1 = name+url, 2 = card type, 3 = color
            if (!spCheckerString.contains("SP") && !spCheckerString.contains("R") && !spCheckerString.contains("S")) {
                StringBuilder str = new StringBuilder(cardListRaw.get(i).child(0).text() + "|");
                str.append(cardListRaw.get(i).child(1).child(0).childNode(0).toString() + "|" + cardListRaw.get(i).child(1).child(0).childNode(2).toString() + "|");//Name
                str.append(cardListRaw.get(i).child(2).text() + "|");
                str.append(cardListRaw.get(i).child(3).text() + "|");
                str.append(cardListRaw.get(i).child(0).child(0).attr("href"));
                String finalString = str.toString();
                cardList.put(cardListRaw.get(i).child(0).text(), finalString);
            }
            i++;
        }
        File parentDir = new File("cardlists");
        if (!parentDir.exists() || !parentDir.isDirectory()) {
            parentDir.mkdir();
        }
        name = name.replace('/', ' ');
        name = name.replace('?', ' ');
        name = name.replace(':', ' ');
        String fileName = name + ".txt";
        File file = new File(parentDir, fileName);
        try {
            file.createNewFile();
            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            for (Map.Entry<String, String> entry : cardList.entrySet()) {
                writer.write(entry.getKey() + "|" + entry.getValue());
                writer.newLine();
            }
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("Download Complete");
    }

    private static void downloadAllCardLists() {
        try {
            BufferedReader br = new BufferedReader(new FileReader("trialdecks.txt"));
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split("\\|");
                downloadSingleCardList("trialdecks", parts[0], parts[1]);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void downloadSingleCard() throws IOException {
        HashMap<String, String> cardData = new HashMap<>();
        Elements singleCardHeadings;
        Elements singleCardData;

        String link = "";

        doc = Jsoup.connect(host + link).get();


        singleCardData = doc.select(".cards3:not([align]), td[colspan=2]>b, .cards2, table[width=95%]>tbody>tr:nth-of-type(3)>td>img");
        singleCardHeadings = doc.select("td.cards");

        for (int i = 0; i < singleCardData.size(); i++) {
            if (i == 0) {
                cardData.put("Eng Name", singleCardData.get(i).text());
            } else if (i == 1) {
                cardData.put("Image Source", singleCardData.get(i).attr("src"));
            } else if (i == 2) {
                cardData.put("Jp Name", singleCardData.get(i).text());
            } else {
                cardData.put(singleCardHeadings.get(i - 2).text().split(":")[0], singleCardData.get(i).text());
            }
        }
    }

    private static void downloadAllCardsOfASeries() {

    }

    private static void checkForUpdate() {

    }
}
