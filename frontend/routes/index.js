const express = require('express');
const axios = require("axios");
const {json} = require("express");
const router = express.Router();

/* GET home page. */
router.get('/', function (req, res, next) {
    res.render('index', {});
});

router.get('/farewell', function (req, res, next) {
    res.render('farewell', {});
});

router.get("/crash", async function (req, res) {
    let crashID = req.query["id"];
    if (crashID === undefined) {
        let requestURL = process.env.backendLocal + "launcher/getCrashReport"
        try {
            let axiosResponse = await axios.get(requestURL);
            let reports = axiosResponse.data["data"];
            res.render("crash/index", {
                id: crashID,
                reports: reports
            });
        } catch (e) {
            // api is unreachable
            console.error("API was unreachable");
            console.error(e);
        }
    } else {
        let requestURL = process.env.backendLocal + "launcher/getCrashReport?id=" + crashID
        // TODO request the api server to get info
        await axios.get(requestURL).then(axiosResponse => {
            let data = axiosResponse.data["data"];
            let trace = data["trace"];
            let type = data["type"];
            let time = data["time"];
            let launchScript = data["launchScript"]
            res.render("crash/result", {
                crashID: crashID,
                trace: trace,
                type: type,
                time: time,
                launchScript: launchScript
            })
        }).catch(e => {
            res.render("crash/result", {
                crashID: "not-found",
                trace: `Crash ID ${crashID} not found`,
                type: "web",
                time: new Date().getTime(),
                launchScript: null
            })
        });
    }
})

router.get('/download/:v', function (req, res, next) {
    res.redirect("https://github.com/CubeWhyMC/LunarClient-CN/releases/" + req.params.v)
});

router.get("/thanks", function (req, res) {
    res.render("thanks"); // process link in the frontend
})

router.get("/download", (req, res) => {
    res.render('download', {
        api: process.env.backend
    });
})

router.get('/help', function (req, res, next) {
    res.redirect("https://github.com/CubeWhyMC/LunarClient-CN/wiki");
});

router.get('/en', function (req, res, next) {
    res.render('index-en', {});
});

router.get('/en/download', function (req, res, next) {
    res.render('download-en', {
        api: process.env.backend
    });
});

router.get("/donate", function (req, res) {
    res.render("donate", {})
});

router.get("/celestial", function (req, res) {
    res.render("celestial/index")
})

router.get("/new", function (req, res) {
    res.redirect("/celestial")
})

module.exports = router;
