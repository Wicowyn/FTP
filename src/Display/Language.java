package Display;

public class Language {

	static final int LOGIN = 1, PASSWORD = 2, HOST = 3, PORT = 4, CONNECT = 5, CONNECTED = 6, DISCONNECTED = 7,
			DELETE = 8, INFO = 9, MOVE = 10, NEWFOLDER = 11, INFO_DIR = 12, INFO_NAME = 13, INFO_TYPE = 14,
			INFO_SIZE = 14, INFO_DATE = 15, INFO_PERM = 16, INFO_OWNER = 17, INFO_GROUP = 18;

	String lang;
	String[] lang_en = { "ENGLISH", "User: ", "Password: ", "Host: ", "Port: ", "Connect", "Connected", "Disconnected",
			"Delete", "Info", "Move", "New Folder", "Directory: ", "Name: ", "Type: ", "Date: ", "Permissions: ",
			"Owner: ", "Group: " };

	String[] lang_fr = { "FRANÇAIS", "Identifiant: ", "Mot De Passe: ", "Serveur: ", "Port: ", "Connecter", "Connecté",
			"Deconnecté", "Supprimer", "Renseignement", "Déplacer", "Nouveau Dossier", "Dossier: ", "Nom: ", "Type: ",
			"Date: ", "Accès: ", "Propriétaire: ", "Groupe: " };

	public Language() {
		lang = System.getProperty("user.language");
	}

	public Language(String l) {
		l = lang;
	}

	public String getPhrase(int phrase) {
		switch (lang) {
		case "en":
			return lang_en[phrase];
		case "fr":
			return lang_fr[phrase];

		}
		return "unspecified";
	}

}
