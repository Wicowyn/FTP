package Display;

public class Language {

	static final int LOGIN = 1;
	static final int PASSWORD = 2;
	static final int HOST = 3;
	static final int PORT = 4;
	static final int CONNECT = 5;
	static final int CONNECTED = 6;
	static final int DISCONNECTED = 7;
	static final int DELETE = 8;
	static final int INFO = 9;
	static final int MOVE = 10;
	static final int NEWFOLDER = 11;
	static final int INFO_DIR = 12;
	static final int INFO_NAME = 13;
	static final int INFO_TYPE = 14;
	static final int INFO_SIZE = 14;
	static final int INFO_DATE = 15;
	static final int INFO_PERM = 16;
	static final int INFO_OWNER = 17;
	static final int INFO_GROUP = 18;

	String lang;
	String[] langEn = { "ENGLISH", "User: ", "Password: ", "Host: ", "Port: ", "Connect", "Connected", "Disconnected",
			"Delete", "Info", "Move", "New Folder", "Directory: ", "Name: ", "Type: ", "Date: ", "Permissions: ",
			"Owner: ", "Group: " };

	String[] langFr = { "FRANÇAIS", "Identifiant: ", "Mot De Passe: ", "Serveur: ", "Port: ", "Connecter", "Connecté",
			"Deconnecté", "Supprimer", "Renseignement", "Déplacer", "Nouveau Dossier", "Dossier: ", "Nom: ", "Type: ",
			"Date: ", "Accès: ", "Propriétaire: ", "Groupe: " };

	public Language() {
		lang = System.getProperty("user.language");
	}

	public Language(String lang) {
		lang = this.lang;
	}

	public String getPhrase(int phrase) {
		switch (lang) {
		case "en":
			return langEn[phrase];
		case "fr":
			return langFr[phrase];
		default:
			return "unspecified";
		}

	}

}
