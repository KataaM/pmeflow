import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:pmeflow/AppContact.dart';
import 'package:pmeflow/AppEntreprise.dart';
import 'package:pmeflow/widget/RatingBox.dart';

import '../../AppColor.dart';

class EntrepriseDetails extends StatelessWidget {
  EntrepriseDetails({Key? key, required this.item}) : super(key: key);
  final AppEntreprise item;

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text(this.item.nom),
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
                            Text(this.item.nom, style: TextStyle(fontWeight: FontWeight.bold)),
                            Text(this.item.mail),
                            Text("Tel: " + item.tel),
                            Text("Site: " + item.site),
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