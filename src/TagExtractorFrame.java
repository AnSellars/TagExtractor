import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import static java.nio.file.StandardOpenOption.CREATE;

public class TagExtractorFrame extends JFrame
{


    JPanel mainPnl;
    JPanel chooserPnl;
    JPanel textAreaPnl;
    JPanel buttonPnl;

    JButton chooserBtn;
    JTextArea filenameTA;

    JTextArea tagsTA;
    JScrollPane scrollPane;

    JButton quitBtn;
    JButton runBtn;
    JButton saveBtn;

    Set<String> stopWords = new TreeSet<>();
    String[] fileArray = new String[0];

    Map <String, Integer>frequencies = new TreeMap<>();

    public TagExtractorFrame() throws FileNotFoundException {

        mainPnl = new JPanel();
        mainPnl.setLayout(new BorderLayout());
        setTitle("Tag Extractor");

        createChooserPnl();
        mainPnl.add(chooserPnl,BorderLayout.NORTH);
        createTxtPnl();
        mainPnl.add(textAreaPnl,BorderLayout.CENTER);
        createButtonPnl();
        mainPnl.add(buttonPnl,BorderLayout.SOUTH);
        add(mainPnl);


        Scanner in = new Scanner(new File("src/stop.txt"));
        while (in.hasNext() == true) {
            String s = in.next();
            stopWords.add(s);
        }
        in.close();


        setSize(400,400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);

    }

    private void createChooserPnl()
    {

    chooserPnl = new JPanel();
    chooserPnl.setLayout(new BorderLayout());
    filenameTA = new JTextArea();
    filenameTA.setFont(new Font("Roboto", Font.PLAIN, 24));
    filenameTA.setEditable(false);
    chooserPnl.add(filenameTA,BorderLayout.SOUTH);

    JFileChooser fileChoose = new JFileChooser();

    chooserBtn = new JButton("Choose File");
    chooserBtn.setFont(new Font("Roboto", Font.PLAIN, 24));
    chooserBtn.addActionListener((ActionEvent ae) ->
    {

        try {
            File workingDirectory = new File(System.getProperty("user.dir"));

            fileChoose.setCurrentDirectory(workingDirectory);

            if (fileChoose.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                File toImport = fileChoose.getSelectedFile();
                Path file = toImport.toPath();
                InputStream in = new BufferedInputStream(Files.newInputStream(file, CREATE));
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));


                fileArray = reader.readLine().split(" ");
                filenameTA.append(String.valueOf(fileChoose.getSelectedFile()));

                reader.close();
            } else {
                System.out.println("Failed to choose file. Try again.");
                System.exit(0);
            }
        } catch (FileNotFoundException e) {
            System.out.println("File not found!!!");
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    });
    chooserPnl.add(chooserBtn,BorderLayout.NORTH);

    }
    private void createTxtPnl()
    {

        textAreaPnl = new JPanel();
        textAreaPnl.setLayout(new BorderLayout());
        tagsTA = new JTextArea();
        tagsTA.setEditable(false);
        scrollPane = new JScrollPane(tagsTA);
        textAreaPnl.add(scrollPane);

    }

    private void createButtonPnl()
    {

        buttonPnl = new JPanel();
        buttonPnl.setLayout(new GridLayout(1,3));

        runBtn = new JButton("Run");
        runBtn.setFont(new Font("Roboto", Font.PLAIN, 24));
        runBtn.addActionListener((ActionEvent ae) ->
        {
            frequencies.clear();
            for (int i=0; i < fileArray.length; i++) {
                String element = fileArray[i];
                if(!stopWords.contains(element))
                {
                    if(frequencies.get(element) == null)
                    {
                        frequencies.put(element, 1);
                    }
                    else
                    {
                        frequencies.put(element, frequencies.get(element) + 1);
                    }
                    tagsTA.append(element + "   " + frequencies.get(element).toString() + "\n");
                }
            }

        });
        buttonPnl.add(runBtn);

        saveBtn = new JButton("Save");
        saveBtn.setFont(new Font("Roboto", Font.PLAIN, 24));
        saveBtn.addActionListener((ActionEvent ae) ->
        {

            try {
                BufferedWriter writer = new BufferedWriter(new FileWriter(JOptionPane.showInputDialog("Enter file name")));
                for (Map.Entry<String, Integer> entry :
                        frequencies.entrySet()) {

                    writer.write(entry.getKey() + ":"
                            + entry.getValue() + "\n");

                    writer.flush();
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }


        });
        buttonPnl.add(saveBtn);

        quitBtn = new JButton("Quit");
        quitBtn.setFont(new Font("Roboto", Font.PLAIN, 24));
        quitBtn.addActionListener((ActionEvent ae) ->
        {int quit = JOptionPane.showConfirmDialog(null,"Are you sure you want to quit?","Quit Confirm", JOptionPane.YES_NO_OPTION);
            if(quit == JOptionPane.YES_OPTION)
            {
                System.exit(0);
            }
        });

        buttonPnl.add(quitBtn);

    }
}
