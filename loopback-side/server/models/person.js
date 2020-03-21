'use strict';

module.exports = function(Person) {
    Person.validatesUniquenessOf('email', {message: 'email is not unique'});
    if (!(Person.settings.realmRequired || Person.settings.realmDelimiter)) {
        Person.validatesUniquenessOf('email', {message: 'Email already exists'});
    }
};