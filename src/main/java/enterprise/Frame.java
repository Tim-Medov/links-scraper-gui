

package enterprise;

import org.hibernate.Session;
import org.hibernate.Transaction;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Frame extends JFrame implements ActionListener {

    JLabel labelWebsiteUrl;
    JLabel labelResultsCount;

    JTextField textWebsiteUrl;
    JTextField textResultsCount;

    JButton buttonFind;

    public Frame() {

        setTitle("Links Scraper GUI");
        setSize(600, 250);

        labelWebsiteUrl = new JLabel("Enter website url:");
        labelResultsCount = new JLabel("Enter links count:");

        textWebsiteUrl = new JTextField(40);
        textResultsCount = new JTextField(40);

        buttonFind = new JButton("Find links");

        setLayout(new FlowLayout(FlowLayout.CENTER, 100, 15));

        add(labelWebsiteUrl);
        add(textWebsiteUrl);
        add(labelResultsCount);
        add(textResultsCount);
        add(buttonFind);

        buttonFind.addActionListener(this);

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        if (textWebsiteUrl.getText().equals("") || textResultsCount.getText().equals("")) {
            JOptionPane.showMessageDialog(null, "Please complete all fields");
        } else {

            try {
                String websiteRegex = "^(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";
                String resultsCountRegex = "[0-9]+";

                String websiteUrl = textWebsiteUrl.getText();
                String resultsCount = textResultsCount.getText();

                if (websiteUrl.matches(websiteRegex) && resultsCount.matches(resultsCountRegex)) {

                    Scraper scraper = new Scraper(websiteUrl, Integer.parseInt(resultsCount));
                    Thread scraperThread = new Thread(scraper);

                    scraperThread.start();
                    scraperThread.join();

                    Session session = SessionFactory.getSession();
                    Transaction transaction = session.beginTransaction();

                    for (String link : scraper.getLinks()) {
                        WebPage page = new WebPage(link);
                        session.save(page);
                    }

                    transaction.commit();
                    session.close();

                    JOptionPane.showMessageDialog(null, "Links found and stored in the database");
                    this.dispose();

                } else {
                    JOptionPane.showMessageDialog(null, "Invalid data entered");
                }
            } catch (Exception ex) {}
        }
    }
}
