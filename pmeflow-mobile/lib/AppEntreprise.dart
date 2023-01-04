import 'package:contacts_service/contacts_service.dart';
import 'package:flutter/material.dart';

import 'AppColor.dart';

class AppEntreprise{
  final Color color;
  String nom;
  String mail;
  String tel;
  String site;
  String tag;

  AppEntreprise({Key? key, required this.color, required this.nom, required this.mail, required this.tel, required this.site,required this.tag});

  static List<AppEntreprise> getProducts() {
    List<AppEntreprise> items = <AppEntreprise>[];
    items.add(
        AppEntreprise(
            color : AppColors.primaryColor,
            nom :"TEST",
            mail : "test.test@gmail.com",
            tel : "0666666666",
            site : "www.test.fr",
            tag : "test"
        )
    );
    items.add(
        AppEntreprise(
            color : AppColors.primaryColor,
            nom :"TEST",
            mail : "test.test@gmail.com",
            tel : "0666666666",
            site : "www.test.fr",
            tag : "test"
        )
    );
    return items;
  }

}