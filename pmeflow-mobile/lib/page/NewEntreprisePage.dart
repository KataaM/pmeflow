import 'package:flutter/material.dart';
import 'package:pmeflow/AppColor.dart';
import 'package:pmeflow/page/ContactPage.dart';

class NewEntrepriseFragment extends StatefulWidget {
  @override
  _NewEntrepriseFragmentFragmentState createState() => _NewEntrepriseFragmentFragmentState();
}

class _NewEntrepriseFragmentFragmentState extends State {

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: SafeArea(
        child: Column(
            children: <Widget>[
              AppBar(
                title: Text("Nouvelle Entreprise"),
                backgroundColor: AppColors.primaryColor,
              ),

              //Boutton SAVE
              FloatingActionButton(
                child: Icon(Icons.save),
                backgroundColor: AppColors.primaryColor,
                onPressed: () async {

                  /*Navigator.push(context,
                      MaterialPageRoute<void>(
                          builder:(BuildContext context) {
                            return ContactFragment();
                            //return ContactDetails(new AppContact(color: Theme.of(context).errorColor, info: new Contact()));
                          }));*/
                  Navigator.pop(context);
                },),

              Expanded(
                  child:ListView(shrinkWrap: true, children: <Widget>[
                    TextFormField(
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
                      cursorColor: AppColors.primaryColor,
                      maxLength: 25,
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
                      maxLength: 50,
                      decoration: const InputDecoration(
                          labelText: 'Téléphone',
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
                      maxLength: 10,
                      decoration: const InputDecoration(
                          labelText: 'Site Web',
                          hintText: 'Entrer l\'url du site web',
                          border: OutlineInputBorder()),
                      validator: (value) {
                        if (value == null) {
                          return 'Veuillez saisir l\'url du site web';
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