package Dateien;
import Exceptions.NoSuchDB;
import SQL.Sql;
import Sellers.Account;
import Sellers.AccountBasis;
import Sellers.AccountPro;
import Sellers.IdNummer;
import Vehicles.Auto;
import Vehicles.FahrzeugBasis;
import Vehicles.Lkw;

import java.io.*;
import java.text.ParseException;
import java.util.ArrayList;
import java.sql.*;

/**
 * Klasse für einen Database-Reader/Writer
 *
 * @author 1319658
 * @version 2.3
 */

public class Database {

    //Properties of Database Connection
    private final String jdbcURL;
    private final String username;
    private final String password;
    private final Connection con;

    //Constructor for Database
    public Database(String url, String username, String password) throws NoSuchDB {
        try {
            //Set properties
            this.con = DriverManager.getConnection(url, username, password);
            this.jdbcURL = url;
            this.username = username;
            this.password = password;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            throw new NoSuchDB();
        }
    }

    //Notwendige Tables erstellen wenn diese nicht bereits existieren
    public boolean createTables(String[] reqTables, String[] creates, String message) {
        try {
            Statement st = this.con.createStatement();
            try {
                for (String reqTable : reqTables) {
                    //Kontrollieren ob alle benötigten Tables vorhanden sind, wenn nicht Fehler
                    st.executeQuery("SELECT * FROM " + reqTable);
                }
            } catch (SQLException throwable) { //Nicht alle benötigten Tables gefunden
                System.out.println(message);
                //Alle benötigten Tables erstellen
                for (String create : creates) {
                    st.execute(create);
                }
                return true;
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return false;
    }

    //Instanz eines Accounts in Datenbank sichern
    public <T extends AccountBasis> void safeAccount(String tablePrivat, String tablePro, T account)
    {
        try {
            Statement st = this.con.createStatement();
                if (account.getClass().getSimpleName().equals("AccountPro")) { //Kontrollieren welcher Typ
                    st.execute(account.toSql(tablePro)); //Prof. Account in entsprechenden Table schreiben
                } else {
                    st.execute(account.toSql(tablePrivat)); //Privaten Account in entsprechenden Table schreiben
                }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            throw new RuntimeException("Accounts konnten nicht gesichert werden");
        }
    }

    //Alle Accounts aus Datenbank laden
    public ArrayList<AccountBasis> loadAccounts(String tablePrivat, String tablePro) {
        ArrayList<AccountBasis> accounts = new ArrayList<>();
        try {
            Statement st = this.con.createStatement();
            ResultSet result = st.executeQuery("SELECT * FROM " + tablePro); //Alle Pro-Accounts laden
            while (result.next()) {
                String key = result.getString("AccID");
                String mail = result.getString("Mail");
                String pw = result.getString("Passwort");
                String firmenname = result.getString("Firmenname");
                String zeiten = result.getString("Oeffnungszeiten");
                accounts.add(new AccountPro(key, mail, pw, zeiten, firmenname)); //Zur Liste hinzufügen
            }
            result = st.executeQuery("SELECT * FROM " + tablePrivat);//Alle Privat Accounts laden
            while (result.next()) {
                String key = result.getString("AccID");
                String mail = result.getString("Mail");
                String pw = result.getString("Passwort");
                String vorname = result.getString("Vorname");
                String nachname = result.getString("Nachname");
                accounts.add(new Account(key, mail, pw, vorname, nachname)); //Zur Liste hinzufügen
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            throw new RuntimeException("Accounts konnten nicht geladen werden");
        }
        return accounts;
    }

    //Alle Fahrzeuge aus Datenbank laden
    public ArrayList<FahrzeugBasis> loadVehicles(String tableInserate, String tableAutos, String tableLKWs, String tableZusatzLKW, String tableZusatzAuto)
    {
        //Spaltennamen definieren
        String colAutoID = "AutoID";
        String colLkwID = "LkwID";
        String colModID = "ModID";
        String colZaID = "ZaID";
        int accID = -1;
        ArrayList<FahrzeugBasis> vehicles = new ArrayList<>();
        try {
            Statement st = this.con.createStatement();
            Statement stSub = this.con.createStatement();

            //Alle Autos laden
            ResultSet result = st.executeQuery("SELECT * FROM " + tableInserate + " WHERE " + colAutoID + " IS NOT NULL");
            while (result.next()) {
                //Alle benötigten Daten aus Inserat-Table laden
                int autoID = result.getInt(colAutoID);
                accID = getAccID(result); //Key des Account der Fahrzeug inseriert hat
                int zusatzAuto = result.getInt("ZusatzAutoID");
                String erstzulassung = result.getString("Erstzulassung");
                String einstelldatum = result.getString("Einstelldatum");
                int odometer = result.getInt("Odometer");
                int preis = result.getInt("Preis");

                ResultSet resultModel = stSub.executeQuery("SELECT * FROM " + tableAutos + " WHERE " + colModID + "=" + autoID);
                resultModel.next();

                //Alle benötigten Daten aus Auto-Modelle-Table laden
                String marke = resultModel.getString("Marke");
                String modell = resultModel.getString("Modell");
                int leistung = resultModel.getInt("Leistung");
                int gewicht = resultModel.getInt("Gewicht");
                String fahrzeugArt = resultModel.getString("Variante");

                ResultSet resultZusatz = stSub.executeQuery("SELECT * FROM " + tableZusatzAuto+ " WHERE " + colZaID + "=" + zusatzAuto);
                resultZusatz.next();

                //Alle benötigten Daten aus Auto-Zusatzattribut-Table laden
                Boolean sportpaket = resultZusatz.getBoolean("Sportpaket");

                //Auto instanzieren und zur Liste hinzufügen
                vehicles.add(new Auto(String.valueOf(accID), marke, modell, erstzulassung, einstelldatum,
                        String.valueOf(leistung), String.valueOf(gewicht), String.valueOf(odometer), String.valueOf(preis), fahrzeugArt, String.valueOf(sportpaket)));
            }

            //Alle LKWs laden
            result = st.executeQuery("SELECT * FROM " + tableInserate + " WHERE " + colLkwID + " IS NOT NULL"); //Alle Autos
            while (result.next()) {
                //Alle benötigten Daten aus Inserat-Table laden
                int lkwID = result.getInt(colLkwID);
                accID = getAccID(result);
                int zusatzLkw = result.getInt("ZusatzLkwID");
                String erstzulassung = result.getString("Erstzulassung");
                String einstelldatum = result.getString("Einstelldatum");
                int odometer = result.getInt("Odometer");
                int preis = result.getInt("Preis");

                ResultSet resultModel = stSub.executeQuery("SELECT * FROM " + tableLKWs + " WHERE " + colModID + "=" + lkwID);
                resultModel.next();

                //Alle benötigten Daten aus Lkw-Modelle-Table laden
                String marke = resultModel.getString("Marke");
                String modell = resultModel.getString("Modell");
                int leistung = resultModel.getInt("Leistung");
                int gewicht = resultModel.getInt("Gewicht");
                int zuladung = resultModel.getInt("Zuladung");
                float zuglast = resultModel.getFloat("Zuglast");

                ResultSet resultZusatz = stSub.executeQuery("SELECT * FROM " + tableZusatzLKW + " WHERE " + colZaID + "=" + zusatzLkw);
                resultZusatz.next();

                //Alle benötigten Daten aus Lkw-Zusatzattribut-Table laden
                Boolean hydraulik = resultZusatz.getBoolean("Hydraulik");

                //Lkw instanzieren und zur Liste hinzufügen
                vehicles.add(new Lkw(String.valueOf(accID), marke, modell, erstzulassung, einstelldatum,
                        String.valueOf(leistung), String.valueOf(gewicht), String.valueOf(odometer), String.valueOf(preis),
                        String.valueOf(zuladung), String.valueOf(zuglast), String.valueOf(hydraulik)));
            }
        } catch (SQLException | ParseException throwables) {
            throwables.printStackTrace();
            throw new RuntimeException("Fahrzeuge konnten nicht geladen werden");
        }
        return vehicles;
    }

    //Inserat löschen
    public void deleteInserat(FahrzeugBasis vec, String tableInserate, long id)
    {
        //Spaltennamen definieren
        String colPrivAccID = "PrivAccID";
        String colProAccID = "ProAccID";
        String colErstzulassung = "Erstzulassung";
        String colEinstelldatum = "Einstelldatum";
        String colOdometer = "Odometer";
        String colPreis = "Preis";
            try {
                Statement st = this.con.createStatement();

                //Richtige Row löschen in der alle Daten übereinstimmen
                st.execute("DELETE FROM " + tableInserate + " WHERE (" + colPrivAccID + "=" + id + " OR " + colProAccID + "=" + id + ") AND " + colErstzulassung
                + "='" + vec.getErstzulassungString() + "' AND " + colEinstelldatum + "='" + vec.getEinstelldatumString() + "' AND " + colOdometer + "='" + vec.getOdometer()
                + "' AND " + colPreis + "='" + vec.getPreis() + "'");
            } catch (SQLException throwables) {
                throwables.printStackTrace();
                throw new RuntimeException("Fahrzeuge konnten nicht gelöscht werden");
            }
    }

    //Account und damit auch zugehörige Inserate löschen (automatisch über Foreign-Key-Verbindung)
    public void deleteAcc(AccountBasis acc, String tablePriv, String tablePro)
    {
        //Spaltennamen definieren
        String colAccID = "AccID";
            try {
                Statement st = this.con.createStatement();
                long id = acc.getKey(); //ID zum Account der gelöscht werden soll

                //Sowohl Privat-Account-Table als auch Pro-Account-Table auf ID des Accounts kontrollieren und wenn gefunden löschen
                st.execute("DELETE FROM " + tablePriv + " WHERE " + colAccID + "=" + id);
                st.execute("DELETE FROM " + tablePro + " WHERE " + colAccID + "=" + id);
            } catch (SQLException throwables) {
                throwables.printStackTrace();
                throw new RuntimeException("Account konnte nicht gelöscht werden"); }
    }

    //Instanz eines Fahrzeugs in Datenbank sichern
    public <T extends FahrzeugBasis> void safeVehicle(T vec, String tableInserate, String tableAuto, String tableLkws, String tableZusatzLkw,
                                 String tableZusatzAuto, String tableAccPriv, String tableAccPro)
    {
        //Spaltennamen definieren
        String colModID = "ModID";
        String colSportpaket = "Sportpaket";
        String colHydraulik = "Hydraulik";
        String colMarke = "Marke";
        String colModell = "Modell";
        String colLeistung = "Leistung";
        String colGewicht = "Gewicht";
        String colFahrzeugart = "Variante";
        String colAutoID = "AutoID";
        String colLkwID = "LkwID";
        String colPrivAccID = "PrivAccID";
        String colProAccID = "ProAccID";
        String colErstzulassung = "Erstzulassung";
        String colEinstelldatum = "Einstelldatum";
        String colOdometer = "Odometer";
        String colPreis = "Preis";
        String colZuladung = "Zuladung";
        String colZuglast = "Zuglast";
        String colZusatzAuto = "ZusatzAutoID";
        String colZusatzLkw = "ZusatzLkwID";
        String colAccID = "AccID";
        String colAcc = colProAccID; //Default Value for which acc type

        int accKey = (int) vec.getKey(); //ID des Accounts der das Fahrzeug inseriert hat
        try {
            Statement st = this.con.createStatement();
                if (vec instanceof Auto) //Für Auto
                {
                    Auto auto = (Auto) vec;
                    int modelId = -1;
                    boolean sportpaket = convBool(auto.getSportPaket());
                    int ZaID = getZaID(tableZusatzAuto, colSportpaket, st, sportpaket); //ID des Zusatzattributeintrags mit passendem Wert
                    ResultSet res;

                    //Überprüfen ob Modell bereits gespeichert ist
                    res = st.executeQuery("SELECT * FROM " + tableAuto + " WHERE " +
                            colMarke + "='" + auto.getMarke() + "' AND " +
                            colModell + "='" + auto.getModell() + "' AND " +
                            colLeistung + "='" + auto.getLeistung() + "' AND " +
                            colGewicht + "='" + auto.getGewicht() + "' AND " +
                            colFahrzeugart + "='" + auto.getFahrzeugArt() + "'"
                            );

                    if(!res.next()){ //Existiert Modell in DB?
                        //Modell existiert nicht bereits, neue Row erstellen
                        st.execute("INSERT INTO " + tableAuto + " (" + colMarke + ", " + colModell + ", " + colLeistung + ", " +
                                colGewicht + ", " + colFahrzeugart + ") VALUES('" + auto.getMarke() + "', '" + auto.getModell() + "', '" +
                                auto.getLeistung() + "', '" + auto.getGewicht() + "', '" + auto.getFahrzeugArt() + "')");
                        res = st.executeQuery("SELECT max(" + colModID + ") FROM " + tableAuto); //Modell-ID ermitteln
                        res.next();
                        modelId = res.getInt("max");
                    } else {
                        modelId = res.getInt("ModID");
                    }

                    //Überprüfen ob Key zu privatem Account gehört
                    res = st.executeQuery("SELECT * FROM " + tableAccPriv + " WHERE " + colAccID + "=" + auto.getKey());
                    if(res.next()) //ID von privatem Account?
                    {
                        colAcc = colPrivAccID;
                    }

                    //Inserat speichern
                    st.execute("INSERT INTO " + tableInserate + " (" + colAutoID + ", " + colAcc + ", " + colErstzulassung + ", " +
                            colEinstelldatum + ", " + colOdometer + ", " + colPreis + ", " + colZusatzAuto + ") VALUES('" + modelId +
                            "', '" + accKey + "', '" + auto.getErstzulassungString() + "', '" + auto.getEinstelldatumString() + "', '" + auto.getOdometer() +
                            "', '" + auto.getPreis() + "', '" + ZaID + "')");

                } else
                {
                    if (vec instanceof Lkw) //Für Lkw
                    {
                        Lkw lkw = (Lkw) vec;
                        int modelId = -1;
                        boolean hydraulik = convBool(lkw.getHydraulik());
                        int ZaID = getZaID(tableZusatzLkw, colHydraulik, st, hydraulik); //ID des Zusatzattributeintrags mit passendem Wert
                        ResultSet res;

                        //Überprüfen ob Modell bereits gespeichert ist
                        res = st.executeQuery("SELECT * FROM " + tableLkws + " WHERE " +
                                colMarke + "='" + lkw.getMarke() + "' AND " +
                                colModell + "='" + lkw.getModell() + "' AND " +
                                colLeistung + "='" + lkw.getLeistung() + "' AND " +
                                colGewicht + "='" + lkw.getGewicht() + "' AND " +
                                colZuladung + "='" + lkw.getZuladung() + "' AND " +
                                colZuglast + "='" + lkw.getZuglast() + "'"
                        );

                        if(!res.next()){ //Existiert Modell in DB?
                            //Modell existiert nicht bereits, neue Row erstellen
                            st.execute("INSERT INTO " + tableLkws + " (" + colMarke + ", " + colModell + ", " + colLeistung + ", " +
                                    colGewicht + ", " + colZuladung + ", " + colZuglast + ") VALUES('" + lkw.getMarke() + "', '" + lkw.getModell() + "', '" +
                                    lkw.getLeistung() + "', '" + lkw.getGewicht() + "', '" + lkw.getZuladung() + "', '" + lkw.getZuglast() + "')");
                            res = st.executeQuery("SELECT max(" + colModID + ") FROM " + tableLkws); //Modell-ID ermitteln
                            res.next();
                            modelId = res.getInt("max");
                        } else {
                            modelId = res.getInt("ModID");
                        }

                        //Überprüfen ob Key zu privatem Account gehört
                        res = st.executeQuery("SELECT * FROM " + tableAccPriv + " WHERE " + colAccID + "=" + lkw.getKey());
                        if(res.next())
                        {
                            colAcc = colPrivAccID;
                        }

                        //Inserat speichern
                        st.execute("INSERT INTO " + tableInserate + " (" + colLkwID + ", " + colAcc + ", " + colErstzulassung + ", " +
                                colEinstelldatum + ", " + colOdometer + ", " + colPreis + ", " + colZusatzLkw + ") VALUES('" + modelId +
                                "', '" + + accKey + "', '" + lkw.getErstzulassungString() + "', '" + lkw.getEinstelldatumString() + "', '" + lkw.getOdometer() +
                                "', '" + lkw.getPreis() + "', '" + ZaID + "')");
                    }
                }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            throw new RuntimeException("Fahrzeuge konnten nicht gesichert werden");
        }
    }

    //ID des Zusatzattributs mit dem passenden boolschen Wert
    private int getZaID(String tableZusatz, String column, Statement st, boolean value) throws SQLException {
        ResultSet res = st.executeQuery("SELECT * FROM " + tableZusatz + " WHERE " + column + "=" + value);
        res.next(); //Muss existieren da Initialisiert
        return res.getInt("ZaID");
    }

    //Account ID aus Inserat-Row ermitteln (entweder ID des Privat-Accounts oder die des Pro-Accounts gesetzt)
    private int getAccID(ResultSet result) throws SQLException {
        int accID;
        Integer PrivAccID = result.getInt("PrivAccID");
        Integer ProAccID = result.getInt("ProAccID");
        if(PrivAccID < 1) //Ist die ID des Privat-Accounts nicht gesetzt?
        {
            accID = ProAccID.intValue();
        } else {
            accID = PrivAccID.intValue();
        }
        return accID;
    }

    //Aktuell höchste Account-ID ermitteln aus Pro-Accounts und Privat-Accounts
    public int getCurrentID(String tablePrivat, String tablePro) {
        String colAccID = "AccID";
        try {
            Statement st = this.con.createStatement();
            ResultSet res_priv = st.executeQuery("SELECT max(" + colAccID + ") FROM " + tablePrivat); //Höchste ID Privat-Accounts
            res_priv.next();
            int max_id_priv = res_priv.getInt("max");
            ResultSet res_pro = st.executeQuery("SELECT max(" + colAccID + ") FROM " + tablePro); //Höchste ID Pro-Accounts
            res_pro.next();
            try { //Höhere ID ermitteln und zurückgeben
                if (max_id_priv>res_pro.getInt("max"))
                {
                    return max_id_priv;
                } else {
                    return res_pro.getInt("max");
                }
            } catch (org.postgresql.util.PSQLException throwable) //No Column available -> Set initial ID
            {
                return 0;
            }

        } catch (SQLException throwable) {
            throwable.printStackTrace();
            throw new RuntimeException("ID konnte nicht geladen");
        }
    }

    //ja oder nein in boolschen Ausdruck umwandeln
    public boolean convBool(String jaNein)
    {
        return jaNein.equals("ja");
    }
}