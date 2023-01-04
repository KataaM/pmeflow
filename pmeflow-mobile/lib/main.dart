import 'package:contacts_service/contacts_service.dart';
import 'package:flutter/material.dart';
import 'package:permission_handler/permission_handler.dart';
import 'package:pmeflow/AppColor.dart';
import 'package:pmeflow/AppContact.dart';
import 'package:pmeflow/component/ListContact.dart';
import 'package:pmeflow/page/ContactPage.dart';
import 'package:pmeflow/page/EntreprisePage.dart';
import 'package:pmeflow/page/HomePage.dart';
import 'package:pmeflow/page/OpportunitesPage.dart';
import 'package:pmeflow/page/ProjetPage.dart';
import 'package:pmeflow/page/SuiviDuTempsPage.dart';

void main() => runApp(MyApp());


class MyApp extends StatelessWidget {


  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      /*title: 'PME-FLOW',
      theme: ThemeData(
        primarySwatch: Colors.blue,
      ),*/
      home: MyHomePage(title: 'PMEFLOW', key: Key("")),
    );
  }
}

/*class MyHomePage extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar()

    );
  }
}*/


class MyHomePage extends StatefulWidget {
  MyHomePage({required Key key, required this.title}) : super(key: key);

  final String title;

  @override
  _MyHomePageState createState() => _MyHomePageState();
}

class _MyHomePageState extends State<MyHomePage> {
  List<AppContact> contacts = [];
  List<AppContact> contactsFiltered = [];
  Map<String, Color> contactsColorMap = new Map();
  TextEditingController searchController = new TextEditingController();
  bool contactsLoaded = false;
  int _selectedDrawerIndex = 0;
  //Colors test = const Color(0x2a3b4d) as Colors;


  get key => null;

  _getDrawerItemWidget(int pos) {
    switch (pos) {
      case 0:
        return new HomeFragment();
      case 1:
        return new EntrepriseFragment();
      case 2:
        return new ContactFragment(contacts : AppContact.fetchContact());
      case 3:
        return new OpportunitesFragment();
      case 4:
        return new ProjetFragment();
      case 5:
        return new SuiviDuTempsFragment();

      default:
        return new Text("");
    }
  }

  /*@override
  void initState() {
    super.initState();
    getPermissions();
  }
  getPermissions() async {
    if (await Permission.contacts.request().isGranted) {
      getAllContacts();
      searchController.addListener(() {
        filterContacts();
      });
    }
  }

  String flattenPhoneNumber(String phoneStr) {
    return phoneStr.replaceAllMapped(RegExp(r'^(\+)|\D'), (Match m) {
      return m[0] == "+" ? "+" : "";
    });
  }
*/
  /*getAllContacts() async {
    List colors = [
      Colors.green,
      Colors.indigo,
      Colors.yellow,
      Colors.orange
    ];
    int colorIndex = 0;
    List<AppContact> _contacts = (await ContactsService.getContacts()).map((contact) {
      Color baseColor = colors[colorIndex];
      colorIndex++;
      if (colorIndex == colors.length) {
        colorIndex = 0;
      }
      return new AppContact(info: contact, color: baseColor);
    }).toList();
    setState(() {
      contacts = _contacts;
      contactsLoaded = true;
    });
  }*/

  /*filterContacts() {
    List<AppContact> _contacts = [];
    _contacts.addAll(contacts);
    if (searchController.text.isNotEmpty) {
      _contacts.retainWhere((contact) {
        String searchTerm = searchController.text.toLowerCase();
        String searchTermFlatten = flattenPhoneNumber(searchTerm);
        String contactName = contact.info.displayName.toLowerCase();
        bool nameMatches = contactName.contains(searchTerm);
        if (nameMatches == true) {
          return true;
        }

        if (searchTermFlatten.isEmpty) {
          return false;
        }

        var phone = contact.info.phones.firstWhere((phn) {
          String phnFlattened = flattenPhoneNumber(phn.value);
          return phnFlattened.contains(searchTermFlatten);
        }, orElse: () => null);

        return phone != null;
      });
    }
    setState(() {
      contactsFiltered = _contacts;
    });
  }*/
  _contactsPermissions() async {
    PermissionStatus permission = await Permission.contacts.status;
    if (permission != PermissionStatus.granted && permission != PermissionStatus.denied) {
      Map<Permission, PermissionStatus> permissionStatus = await [Permission.contacts].request();
      return permissionStatus[Permission.contacts] ?? PermissionStatus.undetermined;
    } else {
      return permission;
    }
  }

  @override
  Widget build(BuildContext context) {
    bool isSearching = searchController.text.isNotEmpty;
    bool listItemsExist = (
        (isSearching == true && contactsFiltered.length > 0) ||
            (isSearching != true && contacts.length > 0)
    );



    return Scaffold(

      bottomNavigationBar: BottomNavigationBar(
        type: BottomNavigationBarType.fixed,
        currentIndex: _selectedDrawerIndex,
        onTap: (int index) {
          setState(() {
            _selectedDrawerIndex = index;

          });
        },



        items: [

          BottomNavigationBarItem(
            icon: Icon(
                Icons.home,
                //color: Colors.grey,
                size: 40.0
            ),
              //title: new Text('Home')
              label: ''
          ),

          BottomNavigationBarItem(
              icon: Icon(
                  Icons.apartment,
                  //color: Colors.grey,
                  size: 40.0
              ),
              //title: new Text('Home')
              label: ''
          ),

          BottomNavigationBarItem(
              icon: Icon(
                  Icons.contacts,
                  //color: Colors.grey,
                  size: 40.0
              ),
              //title: new Text('Home')
              label: ''
          ),

          BottomNavigationBarItem(
              icon: Icon(
                  Icons.task_alt,
                  //color: Colors.red,
                  size: 40.0
              ),
              //title: new Text('Home')
              label: ''
          ),

          BottomNavigationBarItem(
              icon: Icon(
                  Icons.folder_open,
                  //color: Colors.grey,
                  size: 40.0
              ),
              //title: new Text('Home')
              label: ''
          ),

          BottomNavigationBarItem(
              icon: Icon(
                  Icons.timer,
                  //color: Colors.grey,
                  size: 40.0
              ),
              //title: new Text('Home')
              label: ''
          )

        ],

        //#00b2b2
        //unselectedFontSize: 14,
        unselectedItemColor: AppColors.primaryColor,
        selectedItemColor: AppColors.secondaryColor,
      ),

      body: _getDrawerItemWidget(_selectedDrawerIndex),




      /*appBar: AppBar(
        title: Text(widget.title),
      ),
      floatingActionButton: FloatingActionButton(
        child: Icon(Icons.add),
        backgroundColor: Theme.of(context).primaryColorDark,
        onPressed: () async {
          new ContactsService();
          /*try {

            Contact contact = await ContactsService.openContactForm();
            if (contact != null) {
              getAllContacts();
            }
          } on FormOperationException catch (e) {
            switch(e.errorCode) {
              case FormOperationErrorCode.FORM_OPERATION_CANCELED:
              case FormOperationErrorCode.FORM_COULD_NOT_BE_OPEN:
              case FormOperationErrorCode.FORM_OPERATION_UNKNOWN_ERROR:
                print(e.toString());
            }
          }*/
        },
      ),
      body: Container(
        padding: EdgeInsets.all(20),
        child: Column(
          children: <Widget>[
            Container(
              child: TextField(
                controller: searchController,
                decoration: InputDecoration(
                    labelText: 'Search',
                    border: new OutlineInputBorder(
                        borderSide: new BorderSide(
                            color: Theme.of(context).primaryColor
                        )
                    ),
                    prefixIcon: Icon(
                        Icons.search,
                        color: Theme.of(context).primaryColor
                    )
                ),
              ),
            ),
            contactsLoaded == true ?  // if the contacts have not been loaded yet
            listItemsExist == true ?  // if we have contacts to show
            ContactsList(
              reloadContacts: () {
                getAllContacts();
              },
              contacts: isSearching == true ? contactsFiltered : contacts,key: Key(""),
            ) : Container(
                padding: EdgeInsets.only(top: 40),
                child: Text(
                  isSearching ?'No search results to show' : 'No contacts exist',
                  style: TextStyle(color: Colors.grey, fontSize: 20),
                )
            ) :
            Container(  // still loading contacts
              padding: EdgeInsets.only(top: 40),
              child: Center(
                child: CircularProgressIndicator(),
              ),
            )
          ],
        ),
      ),*/


    );
  }
}