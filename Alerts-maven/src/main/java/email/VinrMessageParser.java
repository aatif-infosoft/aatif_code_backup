package email;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import utils.TargetUser;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;

/**
 * This will parse the XML retrieved from database , compares the the tagName,
 * retrieves and returns the Text - Content of each tag(Node).
 *
 * @author Sushma P
 */
public class VinrMessageParser {

	private Document myDoc;
	private static boolean PLAIN_TEXT_CONTENT = false;
	private static boolean HTML_CONTENT = true;
	private String myToList = null;
	private String myFromList = null;
	private String mySubject = null;
	private String myBody = null;
	private String myCcList = null;
	private String myMobileList = null;
	private String myShortText = null;
	private String myAttachmentName = null;
	private String myNameList = null;
	private ArrayList<TargetUser> targetUsers;
	private ArrayList<TargetUser> informUsers;

	public static String PRIMARY_PROVIDER_CODE = "code";
	public static String PRIMARY_PROVIDER_USERID = "username";
	public static String PRIMARY_PROIVDER_PASSWORD = "password";
	public static String PRIMARY_PROIVDER_URL = "url";
	public static String PRIMARY_PROIVDER_SID = "sid";

	private String myVersion = null;
	private String mySenderId = null;
	private boolean myBodyType = PLAIN_TEXT_CONTENT;
	private boolean forceDefaultProvider = true;

	private HashMap<String, String> primaryProviderParameters = null;
	private String primaryParameterNodeName = null;
	private String primaryParameterTextContent = null;
	private String primarProviderURL = null;

	private String version = null;

	static final Logger logger = Logger.getLogger(VinrMessageParser.class);

	public VinrMessageParser() {
	}

	// Accessors which return the textContent of each node in the XML.
	public List<TargetUser> getToList() {
		return targetUsers;
	}

	public boolean getBodyContentType() {
		return myBodyType;
	}

	public String getFromList() {
		return myFromList;
	}

	public String getSubject() {
		return mySubject;
	}

	public String getBody() {
		return myBody;
	}

	public String getCCList() {
		return myCcList;
	}

	public String getMobileList() {
		return myMobileList;
	}

	public String getShortText() {
		return myShortText;
	}

	public String getAttachmentName() {
		return myAttachmentName;
	}

	public String getName() {
		return myNameList;
	}

	public String getPrimaryParameterNodeName() {
		return primaryParameterNodeName;
	}

	public String getPrimaryParameterTextContent() {
		return primaryParameterTextContent;
	}

	public HashMap<String, String> getPrimaryProviderParameters() {
		return primaryProviderParameters;
	}

	public String getVersion() {
		return myVersion;
	}

	public String getSenderId() {
		if (mySenderId == null || (mySenderId != null && mySenderId.isEmpty() == true)) {
			mySenderId = "groupz";
		}
		return mySenderId;
	}

	/**
	 * This routine gets the String xmlMessage from VinrEmailNotification class,
	 * converts it to the required inputStream. Using the DocumentBuilderFactory we
	 * create a new instance of DocumentBuilder and parses the inputStream to to the
	 * DocumentBuilder instance to obtain DOM Document instances from the XML
	 * document.
	 * 
	 * @param input
	 *            String : Is a concatenation of 'Address' and 'Message' columns of
	 *            'messagesintable'
	 */

	public boolean parse(String xmlMessage) {

		try {
			logger.debug("In VinrMessageParser class using log4j      The XML is :  " + xmlMessage);
			InputStream is = new ByteArrayInputStream(xmlMessage.getBytes("UTF-8"));
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

			DocumentBuilder db = dbf.newDocumentBuilder();
			myDoc = db.parse(is);
			myDoc.getDocumentElement().normalize();
			extractFields();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

	/**
	 * This routine is for making a call to the routines which return the
	 * TextContent of the respective 'Node' like the <tolist>, <from>, <subject>,
	 * etc.. nodes
	 */

	private void processNode(Node node) {

		String tagName = node.getNodeName();
		logger.debug("Tag name : " + tagName);

		if (tagName.equalsIgnoreCase("tolist")) {
			processToList(node);
		}

		if (tagName.equalsIgnoreCase("cclist")) {
			processCcList(node);
		}

		if (tagName.equalsIgnoreCase("inform")) {
			processInformList(node);
		}

		if (tagName.equalsIgnoreCase("from")) {
			processFrom(node);
		}

		if (tagName.equalsIgnoreCase("subject")) {
			processSubject(node);
		}

		if (tagName.equalsIgnoreCase("body")) {
			processEmailbody(node);
		}

		if (tagName.equalsIgnoreCase("shorttext")) {
			processShorttext(node);
		}

		if (tagName.equalsIgnoreCase("type")) {
			processEmailBodyType(node);
		}

		if (tagName.equalsIgnoreCase("attach")) {
			processAttachment(node);
		}

		if (tagName.equalsIgnoreCase("version")) {
			processVersion(node);
			this.version = myVersion;
		}

		if (tagName.equalsIgnoreCase("provider")) {
			processPrimaryProvider(node);
		}

		if (tagName.equalsIgnoreCase("sid")) {
			processSenderId(node);
		}

		return;
	}

	/**
	 * This routine returns the text-content of '<tolist>' Node The <tolist>
	 * contains the address column of messagesintable (email-id when msgType=0,
	 * mobileNumber when msgType=1, email-id and mobileNumber when msgType=2,
	 * system-id, user-id, password, etc. depending on the provider)
	 *
	 * @param input
	 *            String : Node whose text-content is to be extracted.
	 */

	private String getTagContent(Node node, String tagName) {
		String nodeTagName = node.getNodeName();

		if (tagName == null || tagName.trim().isEmpty()) {
			return null;
		}

		if (nodeTagName.equalsIgnoreCase(tagName)) {
			String value = node.getTextContent();
			if (value == null || value.trim().isEmpty()) {
				return null; // force the first tag to be name tag
			} else
				return value;
		}
		return null;
	}

	private void processToList(Node toNode) {
		logger.debug("Inside Process To list");

		// getting the child nodes of <tolist>
		NodeList toList = toNode.getChildNodes();
		if (toList == null)
			return;
		if (targetUsers == null)
			targetUsers = new ArrayList<TargetUser>();

		int totalChildren = toList.getLength();

		// Iterating through the child nodes of <tolist>

		for (int nodeIndex = 0; nodeIndex < totalChildren; nodeIndex++) {
			Node currentNode = toList.item(nodeIndex);
			logger.debug("To List Count Loop : " + nodeIndex);

			NodeList toNodes = currentNode.getChildNodes();
			if (toNodes == null) {
				continue;
			}
			int nodeLength = toNodes.getLength();
			logger.debug("The Number of children are : *******" + nodeLength);

			TargetUser targetUser = new TargetUser();
			boolean nameFlag = false;
			boolean emailFlag = false;
			boolean smsFlag = false;

			// for 'email & sms' option
			for (int nodeCount = 0; nodeCount < nodeLength; nodeCount++) {

				Node node = toNodes.item(nodeCount);

				String value = this.getTagContent(node, "name");
				if (value != null) {
					targetUser.setName(value);
					nameFlag = true;
					continue;
				}

				value = getTagContent(node, "email");
				if (value != null) {
					targetUser.setEmailID(value);
					emailFlag = true;
					continue;
				}
				value = getTagContent(node, "value1");
				if (value != null) {
					targetUser.setValue1(value);
					continue;
				}

				value = getTagContent(node, "contactpersonname");
				if (value != null) {
					targetUser.setContactName(value);
					continue;
				}

				value = getTagContent(node, "prefix");
				if (value != null) {
					targetUser.setPrefix(value);
					continue;
				}

				value = getTagContent(node, "contactpersonprefix");
				if (value != null) {
					targetUser.setContactPersonPrefix(value);
					continue;
				}

				value = getTagContent(node, "fcmid");
				if (value != null) {
					targetUser.setFcmId(value);
					continue;
				}

				value = getTagContent(node, "serverkey");
				if (value != null) {
					targetUser.setServerKey(value);
					continue;
				}

				value = getTagContent(node, "devicetype");
				if (value != null) {
					targetUser.setDeviceType(value);
					continue;
				}

				value = getTagContent(node, "number");
				if (value != null) {
					StringTokenizer strTok = new StringTokenizer(value, ".");
					int noOfTokens = strTok.countTokens();

					if (noOfTokens >= 2) {
						String countryCode = strTok.nextToken();
						String localNo = strTok.nextToken();

						while (strTok.hasMoreTokens()) {
							localNo = strTok.nextToken();
						}

						if (countryCode.equals("+91") || countryCode.equals("91")) {
							targetUser.setMobileNumber("91" + localNo);
							smsFlag = true;
						}
						if (countryCode.equals("+1") || countryCode.equals("1")) {
							targetUser.setMobileNumber("1" + localNo);
							smsFlag = true;
						}
					}
				}
			}
			if (nameFlag == true && (emailFlag == true || smsFlag == true)) {
				this.targetUsers.add(targetUser);
			}
		}
	}

	private TargetUser extractUserInformation(NodeList toNode) {

		if (toNode == null) {
			return null;
		}

		int nodeLength = toNode.getLength();
		logger.debug("The Number of children are : *******" + nodeLength);

		TargetUser userInfo = new TargetUser();
		boolean nameFlag = false;
		boolean emailFlag = false;
		boolean smsFlag = false;

		// for 'email & sms' option
		for (int nodeCount = 0; nodeCount < nodeLength; nodeCount++) {

			Node node = toNode.item(nodeCount);

			String value = this.getTagContent(node, "name");
			if (value != null) {
				userInfo.setName(value);
				nameFlag = true;
				continue;
			}

			value = getTagContent(node, "email");
			if (value != null) {
				userInfo.setEmailID(value);
				emailFlag = true;
				continue;
			}

			value = getTagContent(node, "value1");
			if (value != null) {
				userInfo.setValue1(value);
				continue;
			}

			value = getTagContent(node, "contactpersonname");
			if (value != null) {
				userInfo.setContactName(value);
				continue;
			}

			value = getTagContent(node, "prefix");
			if (value != null) {
				userInfo.setPrefix(value);
				continue;
			}

			value = getTagContent(node, "contactpersonprefix");
			if (value != null) {
				userInfo.setContactPersonPrefix(value);
				continue;
			}

			value = getTagContent(node, "number");
			if (value != null) {
				StringTokenizer strTok = new StringTokenizer(value, ".");
				int noOfTokens = strTok.countTokens();

				if (noOfTokens >= 2) {
					String countryCode = strTok.nextToken();
					String localNo = strTok.nextToken();

					while (strTok.hasMoreTokens()) {
						localNo = strTok.nextToken();
					}

					if (countryCode.equals("+91") || countryCode.equals("91")) {
						userInfo.setMobileNumber("91" + localNo);
						smsFlag = true;
					}
				}
			}
		}

		if (nameFlag == true && (emailFlag == true || smsFlag == true))
			return userInfo;
		else
			return null;
	}

	private void processInformList(Node informNode) {

		// getting the child nodes of <tolist>
		NodeList toList = informNode.getChildNodes();
		if (toList == null)
			return;
		if (informUsers == null)
			informUsers = new ArrayList<TargetUser>();

		int totalChildren = toList.getLength();

		// Iterating through the child nodes of <tolist>

		for (int nodeIndex = 0; nodeIndex < totalChildren; nodeIndex++) {
			Node currentNode = toList.item(nodeIndex);
			logger.debug("Inform To List Count Loop : " + nodeIndex);

			NodeList toNodes = currentNode.getChildNodes();
			if (toNodes == null) {
				continue;
			}
			TargetUser informUser = extractUserInformation(toNodes);
			if (informUser != null) {
				informUsers.add(informUser);
			}
		}
	}

	private void processCcList(Node ccNode) {

		NodeList ccList = ccNode.getChildNodes();
		if (ccList == null)
			return;

		int totalChildren = ccList.getLength();
		for (int nodeIndex = 0; nodeIndex < totalChildren; nodeIndex++) {
			Node currentNode = ccList.item(nodeIndex);
			NodeList ccNodes = currentNode.getChildNodes();
			int ccNodeLength = ccNodes.getLength();
			logger.debug("The Number of children in Cclist are : *******" + ccNodeLength);

			if (ccNodeLength == 3) {
				Node nameNode = ccNodes.item(0);
				String tagName = nameNode.getNodeName();
				if (tagName.equalsIgnoreCase("name") == true) {
					Node firstCcNode = ccNodes.item(1);
					String cctag = firstCcNode.getNodeName();
					logger.debug("CcTag : " + cctag);
					Node secondCcNode = ccNodes.item(2);
					String cctag1 = secondCcNode.getNodeName();
					logger.debug("CcTag1 : " + cctag1);

					if (((cctag.equalsIgnoreCase("email")) && (cctag1.equalsIgnoreCase("number")))
							|| ((cctag.equalsIgnoreCase("number")) && (cctag1.equalsIgnoreCase("email")))) {
						String ccEmailId = "";
						String ccMobNo = "";

						if (cctag.equalsIgnoreCase("email"))
							ccEmailId = firstCcNode.getTextContent();
						else if (cctag1.equalsIgnoreCase("email"))
							ccEmailId = secondCcNode.getTextContent();

						if (myCcList == null)
							myCcList = ccEmailId;
						else
							myCcList = myCcList + "," + ccEmailId;

						if (cctag.equalsIgnoreCase("number"))
							ccMobNo = firstCcNode.getTextContent();
						else if (cctag1.equalsIgnoreCase("number"))
							ccMobNo = secondCcNode.getTextContent();

						logger.debug("The mobile no is : " + ccMobNo);
						StringTokenizer strTok = new StringTokenizer(ccMobNo, ".");
						int noOfTokens = strTok.countTokens();
						if (noOfTokens >= 2) {
							String countryCode = strTok.nextToken();
							String localNo = strTok.nextToken();
							while (strTok.hasMoreTokens()) {
								localNo = strTok.nextToken();
							}
							if (countryCode.equals("+91") || countryCode.equals("91")) {
								if (myMobileList == null)
									myMobileList = "91" + localNo;
								else
									myMobileList = myMobileList + "," + "91" + localNo;
							}
						}
					}
				}
			}

			// To return the textContent of either 'email' or 'sms' Node depending on the
			// msgType
			if (ccNodeLength == 2) {
				Node nameNode = ccNodes.item(0);
				String tagName = nameNode.getNodeName();
				if (tagName.equalsIgnoreCase("name") == true) {
					Node emailNode = ccNodes.item(1);
					String tag = emailNode.getNodeName();
					if (tag.equalsIgnoreCase("email")) {
						String emailId = emailNode.getTextContent();
						if (myCcList == null)
							myCcList = emailId;
						else
							myCcList = myCcList + "," + emailId;
					}
					if (tag.equalsIgnoreCase("number")) {
						String mobNo = emailNode.getTextContent();
						StringTokenizer strTok = new StringTokenizer(mobNo, ".");
						int noOfTokens = strTok.countTokens();
						if (noOfTokens >= 2) {
							String countryCode = strTok.nextToken();
							String localNo = strTok.nextToken();
							while (strTok.hasMoreTokens()) {
								localNo = strTok.nextToken();
							}
							if (countryCode.equals("+91") || countryCode.equals("91")) {
								if (myMobileList == null)
									myMobileList = "91" + localNo;
								else
									myMobileList = myMobileList + "," + "91" + localNo;
							}
						}
					}
				}
			}
		}
	}

	/**
	 * This returns the textContent of <shorttext>
	 *
	 * @param input
	 *            String : shortText Node
	 */
	private void processShorttext(Node smsNode) {
		String nodeName = smsNode.getNodeName();
		if (nodeName.equalsIgnoreCase("shorttext")) {
			myShortText = smsNode.getTextContent();
		}
		return;
	}

	private void processEmailBodyType(Node typeNode) {
		String nodeName = typeNode.getNodeName();
		if (nodeName.equalsIgnoreCase("type")) {
			String typeString = typeNode.getTextContent();
			if (typeString != null && (typeString.isEmpty() == false)) {
				if (typeString.equalsIgnoreCase("html") == true)
					this.myBodyType = VinrMessageParser.HTML_CONTENT;
			}
		}

	}

	private void processVersion(Node versionNode) {
		String nodeName = versionNode.getNodeName();
		if (nodeName.equalsIgnoreCase("version")) {
			myVersion = versionNode.getTextContent();
		}
		return;
	}

	private void processSenderId(Node sidNode) {
		String nodeName = sidNode.getNodeName();
		if (nodeName.equalsIgnoreCase("sid")) {
			mySenderId = sidNode.getTextContent();
		}
		return;
	}

	// for provider Code
	private void processPrimaryProvider(Node providerNode) {

		primaryProviderParameters = new HashMap<String, String>();
		primaryProviderParameters.clear();
		NodeList nodes = providerNode.getChildNodes();
		boolean codePresent = false;
		boolean uidPresent = false;
		boolean passwdPresent = false;
		forceDefaultProvider = true;

		if (nodes == null)
			return;

		int totalChildren = nodes.getLength();

		// Iterating through the child nodes of <provider>
		for (int nodeIndex = 0; nodeIndex < totalChildren; nodeIndex++) {
			Node currentNode = nodes.item(nodeIndex);
			String nodeName = currentNode.getNodeName();
			String nodeContent = currentNode.getTextContent();

			if (nodeContent == null)
				continue;
			if (nodeContent.trim().isEmpty() == true)
				continue;

			if (nodeName.equalsIgnoreCase(VinrMessageParser.PRIMARY_PROVIDER_CODE)) {
				primaryProviderParameters.put(nodeName, nodeContent);
				codePresent = true;
			}
			if (nodeName.equalsIgnoreCase(VinrMessageParser.PRIMARY_PROVIDER_USERID)) {
				primaryProviderParameters.put(nodeName, nodeContent);
				uidPresent = true;
			}
			if (nodeName.equalsIgnoreCase(VinrMessageParser.PRIMARY_PROIVDER_PASSWORD)) {
				primaryProviderParameters.put(nodeName, nodeContent);
				passwdPresent = true;
			}
		}

		if (codePresent || uidPresent || passwdPresent) {
			forceDefaultProvider = false;
		}
	}

	private void processSubject(Node subjectNode) {
		String nodeName = subjectNode.getNodeName();
		if (nodeName.equalsIgnoreCase("subject")) {
			mySubject = subjectNode.getTextContent();
		}
		return;
	}

	private void processAttachment(Node attachmentNode) {
		String nodeName = attachmentNode.getNodeName();
		if (nodeName.equalsIgnoreCase("attach")) {
			myAttachmentName = attachmentNode.getTextContent();
		}
		return;
	}

	private void processEmailbody(Node bodyNode) {
		String nodeName = bodyNode.getNodeName();
		if (nodeName.equalsIgnoreCase("body")) {
			myBody = bodyNode.getTextContent();
		}
		return;
	}

	private void processFrom(Node fromNode) {

		String nodeName = fromNode.getNodeName();
		logger.debug("Name of thr from node is : " + nodeName);
		NodeList fromNodes = fromNode.getChildNodes();
		int totalChildren = fromNodes.getLength();
		logger.debug("No of children in from node are : " + totalChildren);
		if (fromNodes == null)
			return;

		if (totalChildren == 3) {
			Node nameNode = fromNodes.item(0);
			String firstNodeName = nameNode.getNodeName();
			if (firstNodeName.equalsIgnoreCase("name") == true) {
				Node firstFromTag = fromNodes.item(1);
				String firstFromTagName = firstFromTag.getNodeName();
				logger.debug("Name of the first from-tag is : " + firstFromTagName);
				Node secondFromTag = fromNodes.item(2);
				String secondFromTagName = secondFromTag.getNodeName();
				logger.debug("Name of the second from-tag is : " + secondFromTagName);

				if (((firstFromTagName.equalsIgnoreCase("email")) && (secondFromTagName.equalsIgnoreCase("number")))
						|| ((firstFromTagName.equalsIgnoreCase("number"))
								&& (secondFromTagName.equalsIgnoreCase("email")))) {
					String fromEmailId = "";
					if (firstFromTagName.equalsIgnoreCase("email")) {
						fromEmailId = firstFromTag.getTextContent();
						myFromList = fromEmailId;
					} else if (secondFromTagName.equalsIgnoreCase("email")) {
						fromEmailId = secondFromTag.getTextContent();
						myFromList = fromEmailId;
					}
				}
			}
		}

		if (totalChildren == 2) {
			Node nameNode = fromNode.getFirstChild();
			String tagName = nameNode.getNodeName();
			if (tagName.equalsIgnoreCase("name") == true) {
				Node emailNode = fromNode.getLastChild();
				String tag = emailNode.getNodeName();
				if (tag.equalsIgnoreCase("email")) {
					String emailId = emailNode.getTextContent();
					myFromList = emailId;
				}
			}
		}
	}

	private void traverseDocument(Node rootNode) {
		int totalChildren = 0;
		NodeList nodeList = rootNode.getChildNodes();
		if (nodeList == null) {
			return;
		}
		totalChildren = nodeList.getLength();
		for (int numOfNodes = 0; numOfNodes < totalChildren; numOfNodes++) {
			Node currentNode = nodeList.item(numOfNodes);
			traverseDocument(currentNode);
			processNode(currentNode);
		}
	}

	private boolean extractFields() {

		if (myDoc == null) {
			logger.debug("The XML Document does not seem to be parsed");
			return false;
		}

		Element rootElement = myDoc.getDocumentElement();
		Node rootNode = rootElement.getParentNode();

		if (rootNode.getChildNodes() == null) {
			logger.debug("The XML Document just has header...content is empty");
			return false;
		}
		traverseDocument(rootNode);
		return true;
	}

	public void setPrimarProviderURL(String primarProviderURL) {
		this.primarProviderURL = primarProviderURL;
	}

	public String getPrimarProviderURL() {
		return primarProviderURL;
	}

	public void setForceDefaultProvider(boolean forceDefaultProvider) {
		this.forceDefaultProvider = forceDefaultProvider;
	}

	public boolean isForceDefaultProvider() {
		return forceDefaultProvider;
	}

	public String getPrimaryProviderCode() {
		String providerCode = null;
		if (this.primaryProviderParameters != null && this.primaryProviderParameters.isEmpty() == false) {
			providerCode = this.primaryProviderParameters.get(VinrMessageParser.PRIMARY_PROVIDER_CODE);
		}
		return providerCode;
	}

	public void setInformUsers(ArrayList<TargetUser> informUsers) {
		this.informUsers = informUsers;
	}

	public ArrayList<TargetUser> getInformUsers() {
		return informUsers;
	}
}
