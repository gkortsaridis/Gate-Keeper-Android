package gr.gkortsaridis.gatekeeper.Entities

data class Vault( var id: String = "-1",
                  var account_id : String,
                  var name : String,
                  var color: VaultColor?)