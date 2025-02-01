const fs = require('fs');
const path = require('path');
const express = require('express');
const spdy = require('spdy');
const compression = require('compression');

const appFolder = path.join(__dirname, 'dist/frontend/browser');

const app = express();

app.use(compression());

app.use(express.static(appFolder));

app.get('*', (req, res) => {
  res.sendFile(path.join(appFolder, 'index.html'));
});

const options = {
  key: fs.readFileSync('key.pem'),
  cert: fs.readFileSync('cert.pem')
};

spdy.createServer(options, app).listen(4200, (err) => {
  if (err) {
    console.error('Error while start server HTTP/2:', err);
    return;
  }
  console.log('Server HTTP/2 works on https://localhost:4200');
});
