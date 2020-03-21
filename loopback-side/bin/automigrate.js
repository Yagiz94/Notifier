'use strict';

var app = require('../server/server');
var ds = app.datasources.db_source;
var models = ['Notification'];
ds.automigrate(models, (err) =>{
    if(err){
        throw err;
    }
    console.log("Tables Created Successfuly!");
    ds.disconnect();
    process.exit();
});