import 'package:flutter/material.dart';
import 'package:pmeflow/AppContact.dart';
import 'package:pmeflow/component/AvatarContact.dart';



/*class ContactsList extends StatelessWidget {
  final List<AppContact> listContacts;
  Function() reloadContacts;
  ContactsList(this.listContacts, this.reloadContacts);

  @override
  Widget build(BuildContext context) {
    return Expanded(
      child: ListView.builder(
        shrinkWrap: true,
        itemCount: listContacts.length,
        itemBuilder: (context, index) {
          AppContact contact = listContacts[index];

          return ListTile(
              onTap: () {
                reloadContacts();
                Navigator.of(context).push(MaterialPageRoute(
                    builder: (BuildContext context) => ContactDetails(
                      contact[index]

                      //reloadContacts(),
                      //reloadContacts()

                      /*onContactDelete: (AppContact _contact) {
                        reloadContacts();
                        Navigator.of(context).pop();
                      },
                      onContactUpdate: (AppContact _contact) {
                        reloadContacts();
                      },*/
                    )

                ));
                reloadContacts();
              },
              title: Text(contact.nom.toUpperCase()),
              subtitle: Text(
                  contact.tel/*.length > 0 ? contact.info.phones.elementAt(0).value : ''*/
              ),
              leading: /*ContactAvatar(contact, 36)*/Container(),


          );
        },
      ),
    );
  }
}*/