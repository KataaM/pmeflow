import 'package:contacts_service/contacts_service.dart';
import 'package:flutter/material.dart';
import 'package:permission_handler/permission_handler.dart';
import 'package:pmeflow/AppColor.dart';
import 'package:pmeflow/AppContact.dart';
import 'package:pmeflow/component/ListContact.dart';
import 'package:pmeflow/page/NewContactPage.dart';
import 'package:pmeflow/page/NewEntreprisePage.dart';
import 'package:pmeflow/page/details/EntrepriseDetails.dart';
import 'package:pmeflow/widget/ContactBox.dart';
import 'package:pmeflow/widget/EntrepriseBox.dart';

import '../AppEntreprise.dart';

class EntrepriseFragment extends StatelessWidget {
  final entreprises = AppEntreprise.getProducts();



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
          title: Text("Entreprise"),
          backgroundColor: AppColors.primaryColor,
        ),
        floatingActionButton: FloatingActionButton(
          child: Icon(Icons.add),
          backgroundColor: AppColors.primaryColor,
          onPressed: () async {
            //new ContactsService();
            print(entreprises.length);
            if(entreprises.isEmpty) {
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
                      return NewEntrepriseFragment();
                      //return ContactDetails(new AppContact(color: Theme.of(context).errorColor, info: new Contact()));
                    }));

          },
        ),
        body: ListView.builder(
            itemCount: entreprises.length,
            itemBuilder: (context, index) {
              return GestureDetector(
                  child: EntrepriseBox(item: entreprises[index]),
                  onTap: () {
                    Navigator.push(
                      context,
                      MaterialPageRoute(
                        builder: (context) => EntrepriseDetails(item: entreprises[index]),
                      ),
                    );
                  }
              );
            }
        )


    );


  }


}