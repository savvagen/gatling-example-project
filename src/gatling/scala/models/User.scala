package models

class User(id: Int, name: String, username: String, email: String){

  def getName() = {
    this.name
  }

  def getId() = {
    this.id
  }

  def getUsername() = {
    this.username
  }

  def getEmail() = {
    this.email
  }


}