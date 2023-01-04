import 'package:flutter/material.dart';
import 'package:pmeflow/AppColor.dart';
import 'package:pmeflow/AppContact.dart';
import 'package:pmeflow/page/ContactPage.dart';
import 'package:pmeflow/page/EntreprisePage.dart';

class NewContactFragment extends StatefulWidget {
  @override
  //NewContactFragment(this.contact);

  //final AppContact contact;

  _NewContactFragmentFragmentState createState() => _NewContactFragmentFragmentState();
}

class _NewContactFragmentFragmentState extends State<NewContactFragment> {
  TextEditingController _controller_nom = TextEditingController();
  TextEditingController _controller_prenom = TextEditingController();


  late String nom="";
  late String prenom="";
  late String mail="";
  late String tel="";
  late String ville="";
  late String tag="";



  @override
  void dispose() {
    _controller_nom.dispose();
    _controller_prenom.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    //AppContact contact1 = contact


    return Scaffold(
      floatingActionButton: //Boutton SAVE
      FloatingActionButton(
        child: Icon(Icons.save),
        backgroundColor: AppColors.primaryColor,
        onPressed: () async {

          Navigator.pop(context);

          if(_controller_nom.text.isNotEmpty || _controller_prenom.text.isNotEmpty) {

            //widget.contact.donneesPerso.nom = _controller_nom.text;
            //widget.contact.donneesPerso.prenom = _controller_prenom.text;
            //print(widget.contact.nom);

            _controller_nom.clear();
            _controller_prenom.clear();
            //widget.contactUpdate(widget.contact);

            /*var route = new MaterialPageRoute(
                builder: (BuildContext context) => ContactFragment(key: Key(""), c: contact));
            Navigator.of(context).push(route);*/

          } else{
            ScaffoldMessenger.of(context).showSnackBar(
                const SnackBar(
                    content: Text("Ce champ ne peu pas être vide"))
            );
          }


        },),
      body: SafeArea(
        child: Column(
          children: <Widget>[
            AppBar(
              title: Text("Nouveau Contact"),
              backgroundColor: AppColors.primaryColor,
              ),



            Expanded(
                child:ListView(shrinkWrap: true, children: <Widget>[
                  TextFormField(
                    controller: _controller_nom,
                    cursorColor: AppColors.primaryColor,
                    maxLength: 25,
                    decoration: const InputDecoration(
                        labelText: 'Nom',
                        hintText: 'Entrer le nom',
                        border: OutlineInputBorder()),
                    validator: (value) {
                      if (value == null) {
                        return 'Veuillez saisir un nom';
                      }
                      return null;
                    },
                  ),
                  TextFormField(
                    controller: _controller_prenom,
                    cursorColor: AppColors.primaryColor,
                    maxLength: 25,
                    decoration: const InputDecoration(
                        labelText: 'Prénom',
                        hintText: 'Entrer le prénom',
                        border: OutlineInputBorder()),
                    validator: (value) {
                      if (value == null) {
                        return 'Veuillez saisir un prénom';
                      }
                      return null;
                    },
                  ),
                  TextFormField(
                    cursorColor: AppColors.primaryColor,
                    maxLength: 50,
                    decoration: const InputDecoration(
                        labelText: 'Mail',
                        hintText: 'Entrer le mail',
                        border: OutlineInputBorder()),
                    validator: (value) {
                      if (value == null) {
                        return 'Veuillez saisir un mail';
                      }
                      return null;
                    },
                  ),
                  TextFormField(
                    cursorColor: AppColors.primaryColor,
                    maxLength: 10,
                    decoration: const InputDecoration(
                        labelText: 'Mobile',
                        hintText: 'Entrer le numéro',
                        border: OutlineInputBorder()),
                    validator: (value) {
                      if (value == null) {
                        return 'Veuillez saisir un numéro';
                      }
                      return null;
                    },
                  ),
                  TextFormField(
                    cursorColor: AppColors.primaryColor,
                    maxLength: 25,
                    decoration: const InputDecoration(
                        labelText: 'Ville',
                        hintText: 'Entrer la ville',
                        border: OutlineInputBorder()),
                    validator: (value) {
                      if (value == null) {
                        return 'Veuillez saisir une ville';
                      }
                      return null;
                    },
                  ),
                  TextFormField(
                    cursorColor: AppColors.primaryColor,
                    maxLength: 25,
                    decoration: const InputDecoration(
                        labelText: 'Tag',
                        hintText: 'Entrer le tag',
                        border: OutlineInputBorder()),
                    validator: (value) {
                      if (value == null) {
                        return 'Veuillez saisir un tag';
                      }
                      return null;
                    },
                  ),
                ]
            )
            )]
        ),
      ),

    );

  }
}