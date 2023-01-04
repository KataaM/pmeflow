import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:pmeflow/AppContact.dart';
import 'package:pmeflow/widget/RatingBox.dart';

import '../../AppColor.dart';

class ContactDetails extends StatelessWidget {
  ContactDetails({Key? key, required this.item}) : super(key: key);
  final AppContact item;

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text(this.item.donneesPerso.nom),
        backgroundColor: AppColors.primaryColor,
      ),
      body: Center(
        child: Container(
          padding: EdgeInsets.all(0),
          child: Column(
              mainAxisAlignment: MainAxisAlignment.start,
              crossAxisAlignment: CrossAxisAlignment.start,
              children: <Widget>[
                //Image.asset("assets/appimages/" + this.item.image),
                Expanded(
                    child: Container(
                        padding: EdgeInsets.all(5),
                        child: Column(
                          mainAxisAlignment: MainAxisAlignment.spaceEvenly,
                          children: <Widget>[
                            Text(this.item.donneesPerso.nom, style: TextStyle(fontWeight: FontWeight.bold)),
                            Text(this.item.donneesPerso.mail),
                            Text("Tel: " + item.donneesPerso.tel),
                            RatingBox(),
                          ],
                        )
                    )
                )
              ]
          ),
        ),
      ),
    );
  }
}