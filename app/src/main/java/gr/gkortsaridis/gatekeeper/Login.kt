package gr.gkortsaridis.gatekeeper

class Login {

    var name: String
    var username: String
    var password: String
    var url: String
    var notes: String

    constructor(name: String, username: String, password: String, url: String, notes: String) {
        this.name = name
        this.username = username
        this.password = password
        this.url = url
        this.notes = notes
    }

    override fun toString(): String {
        return "Login: [ Name: "+this.name+
                ", Username: "+this.username+
                ", password: "+this.password+
                ", url: "+this.url+
                ", notes: "+this.notes+" ]"
    }
}