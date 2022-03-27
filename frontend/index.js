const express = require('express')
const mustacheExpress = require('mustache-express');
const app = express()
const port = 3000


// Register '.js' extension with The Mustache Express
app.engine('js', mustacheExpress());
app.set('view engine', 'mustache');
app.set('views', __dirname + '/html');

app.get('/main.js', function(req, res){
   var view = {
     backend_url: process.env.BACKEND_URL ? process.env.BACKEND_URL : 'localhost:8080'
   }
   res.render('main.js', view);
})


app.use('/', express.static('html'))

app.listen(port, () => {
  console.log(`Frontend for Macala game running on port: ${port}`)
})