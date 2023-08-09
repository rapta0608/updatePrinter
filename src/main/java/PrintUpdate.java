import com.fasterxml.jackson.databind.ObjectMapper;
import com.jcraft.jsch.*;
import org.json.JSONObject;
import org.springframework.util.StringUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;

public class PrintUpdate {

    public static void main(String[] args) {



        SetEvolisPrint();

        // TODO Auto-generated method stub
        System.out.println("[GUI - JButton과 JLabel 사용해 버튼 클릭 시 카운트 증가 실시]");

        /*[설 명]
         * 1. 자바에서는 JFrame을 사용해서 GUI 프로그램을 만들 수 있습니다
         * 2. JButton : 버튼 객체를 생성합니다
         * 3. JLabel : 라벨 객체 (텍스트 박스) 를 생성합니다
         * 4. 로직 : 부모 프레임 생성 및 설정 > 자식 컴포넌트 생성 및 설정 > 자식 컴포넌트 이벤트 정의 > 부모 프레임에 자식 컴포넌트 추가
         * */

        //TODO 부모 프레임 생성
        JFrame frm = new JFrame("hey-poca");

        frm.setBounds(120, 120, 300, 200); //GUI의 위치와 사이즈 설정
        frm.setLayout(new BorderLayout()); //BorderLayout은 동, 서, 남, 북으로 나뉘어 있는 레이아웃.

        JLabel label = new JLabel("업데이트 진행중"); //텍스트를 보여줄 JLabel 생성
        label.setHorizontalAlignment(JLabel.CENTER); //JLabel 가운데 정렬

        frm.add(label);
        frm.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE); //X버튼 누를시 종료
        frm.setVisible(true); //프레임 보여주기

                    String serverVersion = getServerFileVersion("https://heysome.kr:444/api/v1/app/print/version/46"); // 서버에 있는 파일 버전 정보를 담은 텍스트 파일의 URL을 입력하세요.
                    String filePath = new File("").getAbsolutePath()+"\\version.txt";
                    String localVersion = getLocalFileVersion(filePath); // 로컬에 있는 파일 경로를 입력하세요.


                    if (serverVersion != null && localVersion != null) {
                        

                       /* if (serverVersion.equals(localVersion) || (Integer.parseInt(serverVersion) <= Integer.parseInt(localVersion))) {

                                String strimg = new File("").getAbsolutePath();

                                String osArch = System.getProperty("os.arch");

                                File file = new File(strimg + "\\" + osArch + "\\SmartPrint.exe");

                                try {
                                    Process p = Runtime.getRuntime().exec(strimg + "\\" + osArch + "\\SmartPrint.exe");
                                    BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
                                    String line;

                                    while ((line = reader.readLine()) != null) {
                                        System.out.println(line);
                                    }
                                    p.waitFor();


                                } catch (Exception b) {
                                    txt1.setText("에러가 발생했습니다. 종료 후 재시작해주세요");
                                    b.printStackTrace();
                                }
                        }*/
                            if (Integer.parseInt(serverVersion) > Integer.parseInt(localVersion)) {

                                try {

                                    Session session = null;
                                    JSch jsch = new JSch();
                                    session = jsch.getSession("root", "175.126.123.207", 22);
                                    session.setPassword("!solfocus0510");

                                    session.setConfig("StrictHostKeyChecking", "no");
                                    session.connect();

                                    ChannelSftp channelSftp = (ChannelSftp) session.openChannel("sftp");
                                    channelSftp.connect();


                                    String strimg = new File("").getAbsolutePath();
                                    channelSftp.get("/home/heysomeAPI/idp-print/update/SmartPrint.exe", strimg);


                                    channelSftp.disconnect();
                                    session.disconnect();
                                   


                                    try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath))) {
                                        String newContent = serverVersion;
                                        bw.write(newContent);
                                    } catch (IOException k) {
                                        k.printStackTrace();
                                    }


                                } catch (JSchException | SftpException h) {
                                    h.printStackTrace();
                                }


                            }else {
                            System.out.println("버전 정보를 가져오는 데에 실패하였습니다.");
                        }


                    }




        System.exit(0);

    }//메인 종료


    private static void terminateProcess(String processId) throws IOException, InterruptedException {
        String command = System.getProperty("os.name").startsWith("Windows") ?
                "taskkill /F /PID" : "kill -9";
        String killCommand = command + " " + processId;
        Runtime.getRuntime().exec(killCommand).waitFor();
    }



    public static String getLocalFileVersion(String filePath) {
        String version="0";



        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                System.out.println(line);
                version=line;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return version;
    }
        // 로컬 파일 버전 정보를 가져오는 코드를 구현해야 합니다.
        // 예를 들어, 파일의 내용을 읽어서 버전 정보를 추출하는 방법을 사용할 수 있습니다.
        // 이 예시에서는 단순히 "1.0.0"으로 가정합니다.


    public static String getServerFileVersion(String serverUrl) {
        try {
            ObjectMapper objectMapper=new ObjectMapper();
            URL url = new URL(serverUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String line;
                StringBuilder response = new StringBuilder();

                while ((line = br.readLine()) != null) {
                    response.append(line);
                }


                br.close();

                JSONObject jsonObject = new JSONObject(response.toString());
                JSONObject resultObject = jsonObject.getJSONObject("result");
                String cd_code = (String) resultObject.get("cd_code");

                return cd_code;
            } else {
                System.out.println("서버 연결에 실패하였습니다. 응답 코드: " + conn.getResponseCode());
                return null;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


    private static void SetEvolisPrint() {
        String falsePrint = "";
        String truePrint = "";
        try {
            // 실행할 명령어 설정


            String command = "wmic printer get Name, PortName,  WorkOffline";

            // 프로세스 실행
            Process process = Runtime.getRuntime().exec(command);

            // 프로세스의 출력 스트림을 버퍼드 리더로 읽음
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream(), "Cp949"));

            // 출력 결과 읽기
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains("Evolis")) {
                    if (line.contains("TRUE")) {
                        String[] parts = line.split("\\s{2,}"); // 두 개 이상의 공백으로 분리
                        if (parts.length > 0) {

                            String name = parts[0].trim(); // "Name" 컬럼 값 추출

                            falsePrint = name;
                        }
                    }
                    if (line.contains("FALSE")) {
                        String[] parts = line.split("\\s{2,}"); // 두 개 이상의 공백으로 분리
                        if (parts.length > 0) {

                            String name = parts[0].trim(); // "Name" 컬럼 값 추출
                            truePrint = name;
                        }
                    }
                }
            }


            System.out.println(falsePrint);
            System.out.println(truePrint);



            // 프로세스 종료 대기
            process.waitFor();
            if (StringUtils.hasText(falsePrint) && StringUtils.hasText(truePrint)) {
                try {
                    // 이전 프린터 이름과 새로운 프린터 이름을 자바 변수로 저장
                    String oldPrinterName = falsePrint;
                    String newPrinterName = falsePrint + " off";

                    // 배치 파일 경로 지정 (경로를 적절히 수정해주세요)


                    String strimg = new File("").getAbsolutePath();
                    String batchFilePath = strimg + "\\changeName.bat";

                    // ProcessBuilder를 사용하여 배치 파일 실행
                    ProcessBuilder builder = new ProcessBuilder("cmd.exe", "/c", batchFilePath, oldPrinterName, newPrinterName);

                    // 프로세스 실행
                    Process process2 = builder.start();

                    // 프로세스가 완료될 때까지 대기
                    process2.waitFor();

                    System.out.println("프린터 이름 변경이 완료되었습니다.");

                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
            if (StringUtils.hasText(truePrint)) {
                try {
                    // 이전 프린터 이름과 새로운 프린터 이름을 자바 변수로 저장
                    String oldPrinterName = truePrint;
                    String newPrinterName = "Evolis KC Prime";

                    // 배치 파일 경로 지정 (경로를 적절히 수정해주세요)


                    String strimg = new File("").getAbsolutePath();
                    String batchFilePath = strimg + "\\changeName.bat";

                    // ProcessBuilder를 사용하여 배치 파일 실행
                    ProcessBuilder builder = new ProcessBuilder("cmd.exe", "/c", batchFilePath, oldPrinterName, newPrinterName);

                    // 프로세스 실행
                    Process process2 = builder.start();

                    // 프로세스가 완료될 때까지 대기
                    process2.waitFor();

                    System.out.println("프린터 이름 변경이 완료되었습니다.");

                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }
            }

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        if (StringUtils.hasText(truePrint)) {
            try {
                // 명령어 설정
                String rundll32Command = "RUNDLL32 PRINTUI.DLL,PrintUIEntry /y /n \"Evolis KC Prime\"";

                // 프로세스 빌더 생성
                ProcessBuilder processBuilder = new ProcessBuilder("cmd.exe", "/c", rundll32Command);

                // 프로세스 실행
                Process process = processBuilder.start();

                // 프로세스 종료 대기
                int exitCode = process.waitFor();

                if (exitCode == 0) {
                    System.out.println("명령어 실행 성공!");
                } else {
                    System.err.println("명령어 실행 실패! (Exit Code: " + exitCode + ")");
                }

            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}