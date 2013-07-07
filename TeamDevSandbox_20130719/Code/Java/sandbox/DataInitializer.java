package sandbox;

import java.util.HashSet;

import lotus.domino.Database;
import lotus.domino.Document;

public class DataInitializer {

	private static final boolean TRACE = false;

	// Current database
	Database db;

	// Delete?
	boolean deleteAllDoc;

	// Users
	boolean createUsers;
	int users_maxUsers;

	// Form names
	String[] forms = {
			"Contact",
			"Contact01",
			"Contact02",
			"Contact03",
			"Contact04",
			"Contact05",
			"Contact06",
			"Contact07",
			"Contact08",
			"Contact09",
			"Contact10",
			"Contact11",
			"Contact12",
			"Contact13",
			"Contact14",
			"Contact15",
			"Contact16",
			"Contact17",
			"Contact18",
			"Contact19",
			"Contact20",
			"Contact21",
			"Contact22",
			"Contact23",
			"Contact24",
			"Contact25",
			"Contact26",
			"Contact27",
			"Contact28",
			"Contact29",
			"Contact30",
			};

	public DataInitializer(Database db) {
		if (TRACE) {
			System.out.println("DataInitializer() is called.");
		}
		this.db = db;
	}

	// ===================================================================
	// Import data
	// ===================================================================

	public void run() throws Exception {
		if (deleteAllDoc) {
			deleteAllDocuments();
		}
		if (createUsers) {
			createUsers();
		}
	}

	public void initDeleteDocuments() throws Exception {
		this.deleteAllDoc = true;
	}

	public void initUsers(int maxUsers) throws Exception {
		if (TRACE) {
			System.out.println("initUsers() is called with maxUser = " + maxUsers);
		}

		this.createUsers = true;
		this.users_maxUsers = maxUsers;
	}

	// ===================================================================
	// Delete all documents
	// ===================================================================

	void deleteAllDocuments() throws Exception {
		if (TRACE) {
			System.out.println("deleteAllDocuments() is called.");
		}

		try {
			db.getAllDocuments().removeAll(true);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	// ===================================================================
	// Contacts
	// ===================================================================

	private static final boolean UNIQUE_USERS = true;

	void createUsers() throws Exception {
		if (TRACE) {
			System.out.println("createUsers() is called.");
		}

		String[] firstNameEntries = SampleDataUtil.readFirstNameEntries();
		String[] lastNameEntries = SampleDataUtil.readLastNameEntries();
		String[] cityEntries = SampleDataUtil.readCityEntries();

		HashSet<String> users = UNIQUE_USERS ? new HashSet<String>() : null;

		// loop counter
		int count = 0;

		for (int j = 0; j < forms.length; j++) {
			String form = forms[j];

			for (int i = 0; i < users_maxUsers; i++) {
				while (true) {
					String fnEntry = firstNameEntries[(int) (Math.random() * firstNameEntries.length)];
					String firstName = SampleDataUtil.getPrimary(fnEntry);
					String altFirstName = SampleDataUtil.getAlternate(fnEntry);

					String lnEntry = lastNameEntries[(int) (Math.random() * lastNameEntries.length)];
					String lastName = SampleDataUtil.getPrimary(lnEntry);
					String altLastName = SampleDataUtil.getAlternate(lnEntry);

					String cityEntry = cityEntries[(int) (Math.random() * cityEntries.length)];
					String city = SampleDataUtil.getCity(cityEntry);
					String prefecture = SampleDataUtil.getPrefecture(cityEntry);

					String email = createEmail(firstName, lastName);
					String id = "CN=" + firstName + " " + lastName
							+ "/O=renovations";

					count++;
					if (count > firstNameEntries.length
							* lastNameEntries.length) {
						throw new Exception("Generating user may enter infinite loop:"
										+ " forms.length=" + forms.length
										+ ", users_maxUsers=" + users_maxUsers
										+ ", firstNameEntries.length="
										+ firstNameEntries.length
										+ ", lastNameEntries.length="
										+ lastNameEntries.length);
					}

					// If user already there, then reject and continue
					// Else, create it...
					String nn = lastName + " " + firstName;
					if (users == null || !users.contains(nn)) {
						if (users != null) {
							users.add(nn);
						}
						createUser(db, form, id, firstName, lastName,
								altFirstName, altLastName, city, prefecture,
								email);
						break;
					}
				}
			}
		}

	}

	String createEmail(String firstName, String lastName) {
		StringBuilder b = new StringBuilder();
		for (int i = 0; i < firstName.length(); i++) {
			char c = Character.toLowerCase(firstName.charAt(i));
			if (c >= 'a' && c <= 'z') {
				b.append(c);
			}
		}
		b.append('_');
		for (int i = 0; i < lastName.length(); i++) {
			char c = Character.toLowerCase(lastName.charAt(i));
			if (c >= 'a' && c <= 'z') {
				b.append(c);
			}
		}
		b.append("@");
		b.append("renovations.com");
		return b.toString();
	}

	void createUser(Database db, String form, String id, String firstName,
			String lastName, String altFirstName, String altLastName,
			String city, String prefecture, String email) throws Exception {
		if (TRACE) {
			System.out.println("Registering user with " + form + "," + id + ","
							+ firstName + "," + lastName + "," + altFirstName
							+ "," + altLastName + "," + city + "," + prefecture
							+ "," + email);
		}

		Document doc = null;
		
		try { 
			doc = db.createDocument();

			doc.replaceItemValue("Form", form);
			doc.replaceItemValue("Id", id);
			doc.replaceItemValue("FirstName", firstName);
			doc.replaceItemValue("LastName", lastName);
			doc.replaceItemValue("AltFirstName", altFirstName);
			doc.replaceItemValue("AltLastName", altLastName);
			doc.replaceItemValue("City", city);
			doc.replaceItemValue("Prefecture", prefecture);
			doc.replaceItemValue("Email", email);
			doc.save();

		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			if (null != doc) {
				doc.recycle();
			}
		}
	}
}
