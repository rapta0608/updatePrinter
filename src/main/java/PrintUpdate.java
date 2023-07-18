import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

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
        frm.setSize(350, 300);

        //TODO 부모 프레임을 화면 가운데에 배치
        frm.setLocationRelativeTo(null);

        //TODO 부모 프레임을 닫았을 때 메모리에서 제거되도록 설정
        frm.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //TODO 부모 레이아웃 설정
        frm.getContentPane().setLayout(null);

        //TODO 자식 컴포넌트 생성
        JButton btn1 = new JButton("시작");
        JButton btn2 = new JButton("종료");
        JLabel txt1 = new JLabel("준비");

        //TODO 자식 컴포넌트  버튼 위치와 크기 설정
        btn1.setBounds(30, 170, 122, 30); //setBounds(가로위치, 세로위치, 가로길이, 세로길이);
        btn2.setBounds(182, 170, 122, 30); //setBounds(가로위치, 세로위치, 가로길이, 세로길이);
        txt1.setBounds(120, 50, 90, 50); //setBounds(가로위치, 세로위치, 가로길이, 세로길이);
        txt1.setHorizontalAlignment(JLabel.CENTER); //텍스트 센터 표시 설정

        //TODO 자식 컴포넌트 이벤트 정의
        ActionListener btn1_action = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                
                String strimg = new File("").getAbsolutePath();

                String osArch = System.getProperty("os.arch");

                File file =new File(strimg+"\\"+osArch+"\\SmartPrint.exe");
                Thread execThread = new Thread(() -> {
                    try {
                        Process p = Runtime.getRuntime().exec(strimg+"\\"+osArch+"\\SmartPrint.exe");
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

}