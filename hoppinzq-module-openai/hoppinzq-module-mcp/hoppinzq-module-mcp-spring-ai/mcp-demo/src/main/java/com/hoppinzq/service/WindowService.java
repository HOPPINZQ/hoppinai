package com.hoppinzq.service;

import com.hoppinzq.service.cmd.CmdUtils;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.apache.commons.io.FileUtils;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * 需要告知AI音乐文件夹的路径和网易云的安装路径（如果要打开网易云VIP音乐的话）
 * 不会使用请联系我
 */
@Service
public class WindowService {
    public static final String music_path = "D:\\CloudMusic";
    private static final String wyy_exe = "E:\\LenovoSoftstore\\Install\\wangyiyunyinyue\\cloudmusic.exe";

    private static List<File> getByCommonsIO(String directoryPath) {
        List<File> fileList = new ArrayList<>();
        FileUtils.listFiles(
                new File(directoryPath),
                new String[]{"mp3", "ncm"}, // mp3正常文件，ncm网易云vip音乐
                true
        ).forEach(fileList::add);
        return fileList;
    }

    @Tool(name = "open_link", description = "打开网站链接")
    public String openLink(@ToolParam(required = true, description = "链接") String link) {
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("windows")) {
            try {
                Runtime.getRuntime().exec("cmd /c start " + link);
                return "打开链接成功";
            } catch (Exception e) {
                return "打开链接失败，" + e.getMessage();
            }
        } else {
            return "当前系统不支持此操作";
        }
    }

    @Tool(name = "open_game", description = "打开游戏")
    public String openGame(@ToolParam(required = true, description = "游戏名称") String gameName) {
        return "todo"; // todo
    }

    @Tool(name = "download_game", description = "下载游戏")
    public String downloadGame(@ToolParam(required = true, description = "游戏名称") String gameName) {
        return "todo"; // todo
    }

    @Tool(name = "find_task_list", description = "查找进程列表")
    public String findTaskList() {
        return CmdUtils.startProcess("tasklist /fi \"status eq running\"");
    }

    @Tool(name = "close_app_process", description = "关闭应用程序进程，调用该方法前，先使用find_task_list查找游戏进程的PID")
    public String closeAPP(@ToolParam(required = true, description = "PID进程") String pid) {
        return CmdUtils.startProcess("taskkill /PID " + pid + " /F");
    }

    @Tool(name = "query_music", description = "查找本地的音乐")
    public List<MusicFileInfo> queryMusic() {
        List<File> fileList = getByCommonsIO(music_path);
        List<MusicFileInfo> musicFileList = new ArrayList<>();
        for (File file : fileList) {
            String name = file.getName();
            musicFileList.add(MusicFileInfo.builder()
                    .musicName(name)
                    .musicPath(file.getAbsolutePath())
                    .musicType(name.substring(name.lastIndexOf(".") + 1))
                    .build());
        }
        musicFileList.sort(Comparator.comparing(MusicFileInfo::getMusicName));
        return musicFileList;
    }

    @Tool(name = "open_music", description = "打开音乐")
    public String openMusic(@ToolParam(required = false, description = "音乐路径，如果音乐打开类型musicOpenType是window，则该参数必填") String music_path,
                            @ToolParam(required = false, description = "音乐名，如果音乐打开类型musicOpenType是web，则该参数必填") String music_name,
                            @ToolParam(description = "音乐打开类型，分为window和web") String musicOpenType) {
        List<MusicFileInfo> musicFileList = new ArrayList<>();
        if ("window".equals(musicOpenType)) {
            String musicType = music_path.substring(music_path.lastIndexOf(".") + 1);
            if ("ncm".equals(musicType)) {
                try {
                    StringBuilder stringBuilder = new StringBuilder("cmd /c start ");
                    stringBuilder.append("\"\"").append(" ");
                    stringBuilder.append("\"").append(wyy_exe).append("\"").append(" ");
                    stringBuilder.append("\"").append(music_path).append("\"");
                    Runtime.getRuntime().exec(stringBuilder.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    Runtime.getRuntime().exec("cmd /c start " + music_path);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return "已找到音乐，正在播放...";
        } else {
            String os = System.getProperty("os.name").toLowerCase();
            if (os.contains("windows")) {
                try {
                    Runtime.getRuntime().exec("cmd /c start " + "http://hoppinzq.com/wukong/index.html?s=" + music_name);
                    return "网页打开音乐成功";
                } catch (Exception e) {
                    e.printStackTrace();
                    return "网页打开音乐失败";
                }
            } else {
                return "不支持的操作系统";
            }
        }
    }
}

@Data
@AllArgsConstructor
@Builder
class MusicFileInfo {
    public String musicName;
    public String musicPath;
    public String musicType;
}
