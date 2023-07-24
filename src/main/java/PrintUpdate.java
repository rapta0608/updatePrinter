import com.fasterxml.jackson.databind.ObjectMapper;
import com.jcraft.jsch.*;
import org.json.JSONObject;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;

public class PrintUpdate {

    public static void main(String[] args) {


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

        //TODO 부모 프레임 크기 설정 (가로, 세로)
        frm.setSize(400, 300);

        //TODO 부모 프레임을 화면 가운데에 배치
        frm.setLocationRelativeTo(null);

        //TODO 부모 프레임을 닫았을 때 메모리에서 제거되도록 설정
        frm.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //TODO 부모 레이아웃 설정
        frm.getContentPane().setLayout(null);

        //TODO 자식 컴포넌트 생성
        JButton btn1 = new JButton("시작");
        JButton btn2 = new JButton("종료");
        JButton btn3 = new JButton("업데이트 확인");
        JLabel txt1 = new JLabel("업데이트 확인중");

        //TODO 자식 컴포넌트  버튼 위치와 크기 설정
        btn1.setBounds(50, 170, 100, 30); //setBounds(가로위치, 세로위치, 가로길이, 세로길이);
        btn2.setBounds(250, 170, 100, 30); //setBounds(가로위치, 세로위치, 가로길이, 세로길이);

        txt1.setBounds(0, 50, 300, 50); //setBounds(가로위치, 세로위치, 가로길이, 세로길이);
        txt1.setHorizontalAlignment(JLabel.CENTER); //텍스트 센터 표시 설정




        txt1.setText("실행가능");
        btn1.setVisible(true);

        //TODO 자식 컴포넌트 이벤트 정의
        ActionListener btn1_action = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                txt1.setText("실행중...");

                Thread executionThread = new Thread(() -> {
                    String serverVersion = getServerFileVersion("https://heysome.kr:444/api/v1/app/print/version/46"); // 서버에 있는 파일 버전 정보를 담은 텍스트 파일의 URL을 입력하세요.
                    String filePath = new File("").getAbsolutePath()+"\\x86\\version.txt";
                    String localVersion = getLocalFileVersion(filePath); // 로컬에 있는 파일 경로를 입력하세요.

                    if (serverVersion != null && localVersion != null) {
                        if (serverVersion.equals(localVersion) || (Integer.parseInt(serverVersion) <= Integer.parseInt(localVersion))) {

                            String strimg = new File("").getAbsolutePath();

                            String osArch = System.getProperty("os.arch");

                            File file = new File(strimg + "\\" + osArch + "\\SmartPrint.exe");
                            Thread execThread = new Thread(() -> {
                                try {
                                    Process p = Runtime.getRuntime().exec(strimg + "\\" + osArch + "\\SmartPrint.exe");
                                    BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
                                    String line;

                                    while ((line = reader.readLine()) != null) {
                                        System.out.println(line);
                                    }

                                    p.waitFor();
                                } catch (Exception b) {
                                    b.printStackTrace();
                                }
                            });

                            execThread.start();
                            txt1.setText("실행");
                            btn1.setVisible(false);
                            btn2.setVisible(true);
                        }
                        if(Integer.parseInt(serverVersion) > Integer.parseInt(localVersion) ){
                            btn1.setVisible(false);
                            txt1.setText("업데이트가 필요합니다.  업데이트를 진행합니다.");

                            try {

                                Session session = null;
                                JSch jsch = new JSch();
                                session = jsch.getSession("root", "175.126.123.207", 22);
                                session.setPassword("!solfocus0510");

                                session.setConfig("StrictHostKeyChecking", "no");
                                session.connect();

                                ChannelSftp channelSftp = (ChannelSftp) session.openChannel("sftp");
                                channelSftp.connect();


                                String strimg = new File("").getAbsolutePath() + "\\x64";
                                channelSftp.get("/home/heysomeAPI/idp-print/update/SmartPrint.exe", strimg);


                                String strimg2 = new File("").getAbsolutePath() + "\\x86";
                                channelSftp.get("/home/heysomeAPI/idp-print/update/SmartPrint.exe", strimg2);


                                channelSftp.disconnect();
                                session.disconnect();
                                txt1.setText("업데이트가 완료되었습니다.다시 실행해주세요.");
                                btn1.setVisible(true);


                                try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath))) {
                                    String newContent = serverVersion;
                                    bw.write(newContent);
                                } catch (IOException k) {
                                    k.printStackTrace();
                                }


                            } catch (JSchException | SftpException h) {
                                h.printStackTrace();
                            }


                        }

                    } else {
                        System.out.println("버전 정보를 가져오는 데에 실패하였습니다.");
                    }



                });

                executionThread.start();


                frm.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


            }

        };
        btn1.addActionListener(btn1_action);

        ActionListener btn2_action = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String programName = "chrome.exe"; // 종료하려는 프로그램의 이름

                Thread executionThread = new Thread(() -> {
                    try {
                        Runtime rt = Runtime.getRuntime();
                        Process p = rt.exec("tasklist");
                        Process p2 = Runtime.getRuntime().exec("netstat -ano");
                        BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
                        BufferedReader reader2 = new BufferedReader(new InputStreamReader(p2.getInputStream()));
                        String line;

                        while ((line = reader2.readLine()) != null) {
                            if (line.contains(":8080")) {
                                String[] parts = line.split("\\s+");
                                String processId = parts[parts.length - 1];
                                terminateProcess(processId);
                            }
                        }

                        p.waitFor();
                        p2.waitFor();
                    } catch (Exception c) {
                        c.printStackTrace();
                    }
                });

                executionThread.start();

// GUI 관련 코드
                frm.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
// 나머지 GUI 코드
                btn1.setVisible(true);
                txt1.setText("종료");

            }
        };
        btn2.addActionListener(btn2_action);



        //TODO 부모 프레임에다가 자식 컴포넌트 추가

        frm.getContentPane().add(btn1);
        frm.getContentPane().add(btn2);
        frm.getContentPane().add(btn3);
        frm.getContentPane().add(txt1);

        //TODO 부모 프레임이 보이도록 설정
        frm.setVisible(true);

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
}