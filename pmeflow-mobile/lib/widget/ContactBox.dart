import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:pmeflow/AppContact.dart';
import 'package:pmeflow/widget/RatingBox.dart';

class ContactBox extends StatelessWidget {ContactBox({Key? key, required this.item}) : super(key: key);
final AppContact item;

Widget build(BuildContext context) {
  return Container(
      padding: EdgeInsets.all(2),
      height: 140,
      child: Card(
        child: Row(
            mainAxisAlignment: MainAxisAlignment.spaceEvenly,
            children: <Widget>[
              //Image.asset("assets/appimages/" + this.item.image),
              Expanded(
                  child: Container(
                      padding: EdgeInsets.all(5),
                      child: Column(
                        mainAxisAlignment: MainAxisAlignment.spaceEvenly,
                        children: <Widget>[
                          Text(this.item.donneesPerso.nom, style: TextStyle(fontWeight: FontWeight.bold)), Text(this.item.donneesPerso.mail),
                          Text("Tel: " + this.item.donneesPerso.nom),
                          RatingBox(),
                        ],
                      )
                  )
              )
            ]
        ),
      )
  );
}
}