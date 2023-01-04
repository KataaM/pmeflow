import 'dart:convert';

import 'package:contacts_service/contacts_service.dart';
import 'package:flutter/material.dart';
import 'package:http/http.dart' as http;
import 'AppColor.dart';

class AppContact{
  //final Color color;
  /*String nom;
  String prenom;
  String mail;
  String tel;
  String ville;
  String tag;*/
  DonneesPerso donneesPerso;

  //AppContact(this.nom, this.prenom, this.mail, this.tel, this.ville, this.tag);
  AppContact({required this.donneesPerso});

  /*factory AppContact.fromMap(Map<String, dynamic> json) {
    return AppContact(
      json['nom'],
      json['prenom'],
      json['email'],
      json['telephone'],
      json['ville'],
      json['tag']
    );
  }
*/
  static List<AppContact> parseContacts(String responseBody) {
    final parsed = json.decode(responseBody).cast<Map<String, dynamic>>();
    return parsed.map<AppContact>((json) => AppContact.fromJson(json)).toList();

  }

  static Future<List<AppContact>> fetchContact() async {
    print("TEST CONNECTION");
    final response = await http.get(Uri.parse('http://192.168.43.254:15001/pmeflow/api/v1/contact/lister'));
    //print("RESPONSE "+response.body);

    if (response.statusCode == 200) {

      /*final List  dataList = jsonDecode(response.body);
      print(dataList[0]); // {text: foo, value: 1, status: true}
      print(dataList[1]); // {text: bar, value: 2, status: false}

      final item = dataList[0];

      //AppContact contact = new AppContact(donneesPerso: DonneesPerso.fromJson(item['donneesPerso']));
      AppContact contact = AppContact.fromJson(item);

      print(item['oid']);
      print("NOM "+contact.donneesPerso.nom);
      print("VILLE "+contact.donneesPerso.prenom);*/


      //return jsonDecode(response.body);
      return parseContacts(response.body);
    } else {
      throw Exception('Unable to fetch products from the REST API');
    }
  }



  factory AppContact.fromJson(Map<String, dynamic>json){

    return AppContact(donneesPerso : DonneesPerso.fromJson(json['donneesPerso']));
  }

  /*static String fromJsonVille(Map<String, dynamic>json){
    print(json['ville']);
    return json['ville'];
  }*/


}

class DonneesPerso{
  String nom;
  String prenom;
  String mail;
  String tel;
  //Adresses adresses;
  String tag;

  DonneesPerso({required this.nom, required this.prenom, required this.mail, required this.tel, /*required this.adresses,*/ required this.tag});

  factory DonneesPerso.fromJson(Map<String, dynamic>json)
  {
    return DonneesPerso(nom: json['nom'], prenom: json['prenom'], mail: json['email'], tel: json['mobile'],/*adresses: Adresses.fromJson(json['adresses']),*/ tag: json['tag']);
  }


}
/*
class Adresses{
  String ville;


  Adresses({required this.ville});

  factory Adresses.fromJson(Map<String, dynamic>json)
  {
    return Adresses(ville: json['ville']);
  }


}*/