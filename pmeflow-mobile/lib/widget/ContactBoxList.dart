import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:pmeflow/page/details/ContactDetails.dart';

import '../AppContact.dart';
import 'ContactBox.dart';

class ContactBoxList extends StatelessWidget {
  final List<AppContact> items;
  ContactBoxList({Key? key, required this.items});

  @override
  Widget build(BuildContext context) {
    return ListView.builder(
      itemCount: items.length,
      itemBuilder: (context, index) {
        return GestureDetector(
          child: ContactBox(item: items[index]),
          onTap: () {
            Navigator.push(
              context, MaterialPageRoute(
              builder: (context) => ContactDetails(item: items[index]),
            ),
            );
          },
        );
      },
    );
  }
}