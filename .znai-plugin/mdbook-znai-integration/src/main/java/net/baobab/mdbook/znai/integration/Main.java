package net.baobab.mdbook.znai.integration;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.regex.Pattern;
import static java.util.stream.Collectors.toList;
import net.baobab.mdbook.znai.integration.database.DataBase;
import net.baobab.mdbook.znai.integration.parser.Parser;
import org.json.JSONArray;

public final class Main {

    private static final Pattern PATTERN = Pattern.compile("(\\{\\{#znai(.*?)endznai\\}\\})");
    private SortedMap<String, String> income = new TreeMap<>();
    private SortedMap<String, String> db = DataBase.getData();
    private static final int SECOND_ELEMENT = 1;

    public static void main(String[] args) throws ClassNotFoundException, IOException {
        Class.forName(DataBase.JDBC_DRIVER);
        if (args.length > 0) {
            if ("supports".equals(args[0])) {
                System.exit(0);
            }
        }
        new Main();
    }

    public Main() throws IOException {

        String inputJson = (new Scanner(System.in)).nextLine();
        // String inputJson = "";try {inputJson = new String(Files.readAllBytes(Paths.get("input.json")));}
        
//        try (BufferedWriter writer = new BufferedWriter(new FileWriter(
//                (new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss"))
//                        .format(new Timestamp(System.currentTimeMillis())) + ".json"
//                , true))) {
//            writer.append(inputJson);
//        }

        // create matcher for pattern p and given string
        // stackoverflow.com/q/237061
        PATTERN.matcher(inputJson)
                .results()
                .forEach(match -> income.put(match.group(1), match.group(2)));
        // delete old data
        List<String> delKey = db.entrySet()
                .stream()
                .filter(e -> !income.containsKey(e.getKey()))
                .map(Map.Entry::getKey)
                .collect(toList());
        DataBase.deleteList(delKey);
        // add new data
        income.entrySet()
                .stream()
                .filter(e -> !db.containsKey(e.getKey()))
                .forEach(e -> DataBase.insert(e.getKey(), Parser.parse(e.getValue())));
        inputJson = DataBase.getData()
                .entrySet()
                .stream()
                .reduce(inputJson,
                        (s, e) -> s.replace(e.getKey(), e.getValue()),
                        (s1, s2) -> null);
        JSONArray json = new JSONArray(inputJson);
        System.out.println(json.getJSONObject(SECOND_ELEMENT).toString());
    }
}
