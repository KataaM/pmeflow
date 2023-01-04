import 'package:contacts_service/contacts_service.dart';
import 'package:flutter/material.dart';
import 'package:permission_handler/permission_handler.dart';
import 'package:pmeflow/AppColor.dart';
import 'package:pmeflow/AppContact.dart';
import 'package:pmeflow/component/ListContact.dart';
import 'package:pmeflow/page/NewContactPage.dart';
import 'package:pmeflow/page/details/ContactDetails.dart';
import 'package:pmeflow/widget/ContactBox.dart';
import 'package:pmeflow/widget/ContactBoxList.dart';

class ContactFragment extends StatelessWidget {
  final Future<List<AppContact>> contacts;

  ContactFragment({Key? key, required this.contacts}) : super(key: key);



  //_ContactFragmentState createState() => _ContactFragmentState();

  @override
  Widget build(BuildContext context) {



    /*bool isSearching = searchController.text.isNotEmpty;
    bool listItemsExist = (
        (isSearching == true && contactsFiltered.length > 0) ||
            (isSearching != true && contacts.length > 0)
    );*/
    return Scaffold(
        appBar: AppBar(
          title: Text("Contacts"),
          backgroundColor: AppColors.primaryColor,
        ),
        floatingActionButton: FloatingActionButton(
          child: Icon(Icons.add),
          backgroundColor: AppColors.primaryColor,
          onPressed: () async {
            //new ContactsService();
            /*print(contacts.length);
            if(contacts.isEmpty) {
              print("TEST EMPTY");
            }
            else{
              //getAllContacts();
              print("TEST NOT EMPTY");
              //ContactDetails(contacts.first, setContacts, setContacts);
            }*/
            Navigator.push(context,
                MaterialPageRoute<void>(
                    builder:(BuildContext context) {
                      //AppContact contact = AppContact(nom: "f", prenom: "d", mail: "s", tel: "q", ville: "a", tag: "h");
                      return NewContactFragment();
                      //return ContactDetails(new AppContact(color: Theme.of(context).errorColor, info: new Contact()));
                    }));

          },
        ),
        body: Center(
          child: FutureBuilder<List<AppContact>>(
            future: contacts, builder: (context, snapshot) {
            print("ERROR");
            if (snapshot.hasError) print(snapshot.error);
              return snapshot.hasData ? ContactBoxList(items: snapshot.requireData)

            // return the ListView widget :
            :Center(child : CircularProgressIndicator());
          },
          ),
        )


    );


  }


/*class _ContactFragmentState extends State {
  List<AppContact> contacts = [];
  List<AppContact> contactsFiltered = [];
  Map<String, Color> contactsColorMap = new Map();
  TextEditingController searchController = new TextEditingController();
  bool contactsLoaded = false;

  @override
  void dispose() {
    super.dispose();
  }

  @override
  void initState() {
    super.initState();
    getPermissions();
  }
  getPermissions() async {
    if (await Permission.contacts
        .request()
        .isGranted) {
      //getAllContacts();
      searchController.addListener(() {
        filterContacts();
      });
    }
  }

  setContacts(AppContact contact) async {
    contacts.add(contact);
    print("TEST SET CONTACT");
  }

  Future<List<AppContact>> getAllContacts() async {

    print(contacts.length);
    contactsLoaded = true;

    print("TEST GET CONTACT");

    return contacts;
  }


  filterContacts() {
    List<AppContact> _contacts = [];
    _contacts.addAll(contacts);
    /*if (searchController.text.isNotEmpty) {
      _contacts.retainWhere((contact) {
        String searchTerm = searchController.text.toLowerCase();
        String searchTermFlatten = flattenPhoneNumber(searchTerm);
        String contactName = contact.nom.toLowerCase();
        bool nameMatches = contactName.contains(searchTerm);
        if (nameMatches == true) {
          return true;
        }

        if (searchTermFlatten.isEmpty) {
          return false;
        }

        var phone = contact.tel;

        return phone != null;
      });
    }*/
    setState(() {
      contactsFiltered = _contacts;
    });
  }

  /*@override
  Widget build(BuildContext context) {


    bool isSearching = searchController.text.isNotEmpty;
    bool listItemsExist = (
        (isSearching == true && contactsFiltered.length > 0) ||
            (isSearching != true && contacts.length > 0)
    );
    return Scaffold(
      appBar: AppBar(
        title: Text("Contacts"),
        backgroundColor: AppColors.primaryColor,
      ),
      floatingActionButton: FloatingActionButton(
        child: Icon(Icons.add),
        backgroundColor: AppColors.primaryColor,
        onPressed: () async {
          //new ContactsService();
          print(contacts.length);
          if(contacts.isEmpty) {
            print("TEST EMPTY");
          }
          else{
            //getAllContacts();
            print("TEST NOT EMPTY");
            //ContactDetails(contacts.first, setContacts, setContacts);
          }
          Navigator.push(context,
              MaterialPageRoute<void>(
                  builder:(BuildContext context) {
                    AppContact contact = AppContact(color: AppColors.primaryColor, nom: "nom1", prenom: "prenom1", mail: "mail1", tel: "tel1", ville: "ville1", tag: "tag1");
                    return NewContactFragment(contact, setContacts);
                    //return ContactDetails(new AppContact(color: Theme.of(context).errorColor, info: new Contact()));
                  }));

        },
      ),
      body: ListView.builder(
        itemCount: contacts.length,
        itemBuilder: (context, index) {
        return GestureDetector(
          child: ContactBox(item: contacts[index]),
          onTap: () {
            Navigator.push(
            context,
              MaterialPageRoute(
                builder: (context) => ContactDetailsTest(item: contacts[index]),
              ),
            );
          }
        );
        }
      )


    );

  }*/
*/
}