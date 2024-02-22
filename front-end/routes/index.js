var express = require('express');
var router = express.Router();
var plant_list = 

/* GET home page. */
router.get('/', function(req, res, next) {
  res.render('index', { title: 'KG4S - Companion Planting', plant_list: plant_list});
});

module.exports = router;
