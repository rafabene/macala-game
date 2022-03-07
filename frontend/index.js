const express = require('express')
const app = express()
const port = 3000

app.use('/', express.static('html'))

app.listen(port, () => {
  console.log(`Frontend for Macala game running on port: ${port}`)
})