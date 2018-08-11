package code_format;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.CharUtil;
import cn.hutool.core.util.StrUtil;

public class Main {

    public static void main(String[] args) {
        String src = "C:/Users/Administrator/Desktop\\aa";
        read(src);

    }

    private static void read(String src) {
        File file = new File(src);
        read(file);
    }

    private static void read(File file) {
        if (file.isDirectory()) {
            File[] listFiles = file.listFiles();
            for (File fl : listFiles) {
                read(fl);
            }
        } else {
            readSimpleFile(file);
        }

    }

    private static void readSimpleFile(File file) {
        List<String> readUtf8Lines = FileUtil.readUtf8Lines(file);
        List<String> list = new ArrayList<String>();
        boolean lastLineIsEmpty = false;
        for (int i = 0; i < readUtf8Lines.size(); i++) {
            String line = readUtf8Lines.get(i);
            if (StrUtil.isBlank(line)) {
                if (!lastLineIsEmpty) {
                    line = StrUtil.trim(line);
                    list.add(line);
                }
                lastLineIsEmpty = true;
            } else {
                line = convertLine(line);
                if (line != null) {
                    list.add(line);
                }
                lastLineIsEmpty = false;
            }
        }
        FileUtil.writeUtf8Lines(list, file);
    }

    private static String convertLine(String line) {
        if (StrUtil.isBlank(line)) {
            return "";
        }
        String lineTrim = line.trim();
        line = line.replace("\t", "    ");
        line = StrUtil.repeat(" ", spaceCount(line)) + line.trim();
        if (lineTrim.startsWith("System.out.println(") && lineTrim.endsWith(");")) {
            return null;
        }
        if (lineTrim.startsWith("console(") && (lineTrim.endsWith(");") || lineTrim.endsWith(")"))) {
            return null;
        }
        if (lineTrim.startsWith("alert(") && (lineTrim.endsWith(");") || lineTrim.endsWith(")"))) {
            return null;
        }
        if (lineTrim.startsWith("//") && isCode(lineTrim)) {
            return null;
        }
        if (lineTrim.startsWith("*")) {
            String curLineTrime = StrUtil.removePrefix(lineTrim, "*");
            curLineTrime = curLineTrime.trim();
            if (curLineTrime.startsWith("@version ")) {
                String now = DateUtil.format(new Date(), "yyyy.MM.dd HH:mm");
                return StrUtil.subBefore(line, "@version ", false) + "@version " + now;
            }
            if (curLineTrime.startsWith("@throws ")) {
                String exceMessage = "异常";
                String after = StrUtil.subAfter(line, "@throws ", false);
                after = StrUtil.nullToDefault(after, "");
                after = after.trim();
                if (isAllLetter(after)) {
                    return StrUtil.subBefore(line, "@throws ", false) + "@throws " + after + " " + exceMessage;
                }
            }
            Map<String, String> paramMap = new HashMap<String, String>();
            paramMap.put("ids", "xuhao");
            paramMap.put("name", "名称");
            Set<Entry<String, String>> entrySet = paramMap.entrySet();
            for (Entry<String, String> entry : entrySet) {
                String key = entry.getKey();
                String value = entry.getValue();
                if (curLineTrime.startsWith("@param ")) {
                    String after = StrUtil.subAfter(line, "@param ", false);
                    after = StrUtil.nullToDefault(after, "");
                    after = after.trim();
                    if (key.equals(after)) {
                        if (isAllLetter(after)) {
                            return StrUtil.subBefore(line, "@param ", false) + "@param " + after + " " + value;
                        }
                    }
                }
            }
        }
        return line;
    }

    private static boolean isAllLetter(String str) {
        if (StrUtil.isBlank(str)) {
            return false;
        }
        for (int i = 0; i < str.length(); i++) {
            char ch = str.charAt(i);
            if (!CharUtil.isLetter(ch)) {
                return false;
            }
        }
        return true;
    }

    private static boolean isCode(String line) {
        line = StrUtil.removePrefix(line, "//");
        line = line.trim();
        if (line.length() > 5 && CharUtil.isLetter(line.charAt(0)) && CharUtil.isLetter(line.charAt(1))
                && line.endsWith(";")) {
            return true;
        }
        return false;
    }

    private static int spaceCount(String str) {
        char[] charArray = str.toCharArray();
        int count = 0;
        for (char ch : charArray) {
            if (ch == ' ') {
                count++;
            } else {
                return count;
            }
        }
        return count;
    }
}
