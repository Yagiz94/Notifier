'use strict';

const app = require('../server/server');
const ds = app.datasources.db_source;
const models = ['Person',
                'Item',
                'Notification',
                'Market'];
ds.autoupdate(models, (err) =>{
    if(err){
        throw err;
    }
    console.log("Models Synced Successfuly!");
    ds.disconnect();
    process.exit();
});