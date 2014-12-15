var canvas = document.getElementById("canvas"),
    ctx = canvas.getContext("2d"),
	ctx2=canvas.getContext("2d"),
    painting = false,
    lastX = 0,
    lastY = 0,
    lineThickness = 5;

   
document.getElementById("brushSize").addEventListener("input",function(){
if(this.value===undefined || this.value==='')
lineThickness = 5 ;
else
lineThickness=this.value ;
});

//var XMob , yMob;
var d;

 var color ="rgba(255,0,0,1)";
	
canvas.width = 1200; 
canvas.height = 600;
//ctx2.fillStyle="rgba(255,255,255,1)";
//ctx2.fillRect(0, 0, 1200, 600);
var c=0;

/*
canvas.onmousedown = function(e) {
    painting = true;
    ctx.fillStyle = color;
    lastX = e.pageX - this.offsetLeft;
    lastY = e.pageY - this.offsetTop;
};

canvas.onmouseup = function(e){
    painting = false;
}

canvas.onmousemove = function(e) {
    if (painting) {
        mouseX = e.pageX - this.offsetLeft;
        mouseY = e.pageY - this.offsetTop;

        // find all points between        
        var x1 = mouseX,
            x2 = lastX,
            y1 = mouseY,
            y2 = lastY;


        var steep = (Math.abs(y2 - y1) > Math.abs(x2 - x1));
        if (steep){
            var x = x1;
            x1 = y1;
            y1 = x;

            var y = y2;
            y2 = x2;
            x2 = y;
        }
        if (x1 > x2) {
            var x = x1;
            x1 = x2;
            x2 = x;

            var y = y1;
            y1 = y2;
            y2 = y;
        }

        var dx = x2 - x1,
            dy = Math.abs(y2 - y1),
            error = 0,
            de = dy / dx,
            yStep = -1,
            y = y1;
        
        if (y1 < y2) {
            yStep = 1;
        }
       
        //lineThickness = 5 - Math.sqrt((x2 - x1) *(x2-x1) + (y2 - y1) * (y2-y1))/10;
        if(lineThickness < 1){
            lineThickness = 1;   
        }

        for (var x = x1; x < x2; x++) {
            if (steep) {
                ctx.fillRect(y, x, lineThickness , lineThickness );
            } else {
                ctx.fillRect(x, y, lineThickness , lineThickness );
            }
            
            error += de;
            if (error >= 0.5) {
                y += yStep;
                error -= 1.0;
            }
        }



        lastX = mouseX;
        lastY = mouseY;

    }
}
*/
function clearIt()
{

//ctx.fillStyle = "rgba(255,255,255,1)";
//ctx.fillRect(0,0,1200,600);
ctx.clearRect(0,0,1200,600);
lastX=d.x;
lastY=d.y;
}

function setColor( name){
console.log(name);
switch(name){
case "red": color='rgba(255,0,0,1)';break;
case "yellow" : color = "rgba(255,255,0,1)";break;
case "blue" : color = "rgba(0,0,255,1)";break;
case "green": color="rgba(0,255,0,1)";break;
case "darkgreen" : color = "rgba(0,150,0,1)";break;
case "lightblue": color="rgba(0,255,255,1)";break;
case "white": color="rgba(255,255,255,1)";break;
default : color="rgba(0,0,0,1)";break;
	}
ctx.fillStyle=color;
}

function move(data) {
    d=JSON.parse(data);
    console.log(d);
    if (d.flag) {
    console.log(d.flag);
        if(c==0)
        {
            ctx.fillStyle = color;
            lastX = d.x;
            lastY = d.y;
            c++;
        }
        mouseX = (d.y)/2;// - this.offsetLeft;
        mouseY = (d.x)/2;// - this.offsetTop;

        // find all points between        
        var x1 = mouseX,
            x2 = lastX,
            y1 = mouseY,
            y2 = lastY;


        var steep = (Math.abs(y2 - y1) > Math.abs(x2 - x1));
        if (steep){
            var x = x1;
            x1 = y1;
            y1 = x;

            var y = y2;
            y2 = x2;
            x2 = y;
        }
        if (x1 > x2) {
            var x = x1;
            x1 = x2;
            x2 = x;

            var y = y1;
            y1 = y2;
            y2 = y;
        }

        var dx = x2 - x1,
            dy = Math.abs(y2 - y1),
            error = 0,
            de = dy / dx,
            yStep = -1,
            y = y1;
        
        if (y1 < y2) {
            yStep = 1;
        }
       
        //lineThickness = 5 - Math.sqrt((x2 - x1) *(x2-x1) + (y2 - y1) * (y2-y1))/10;
        if(lineThickness < 1){
            lineThickness = 1;   
        }

        for (var x = x1; x < x2; x++) {
            if (steep) {
                ctx.fillRect(y, x, lineThickness , lineThickness );
            } else {
                ctx.fillRect(x, y, lineThickness , lineThickness );
            }
            
            error += de;
            if (error >= 0.5) {
                y += yStep;
                error -= 1.0;
            }
        }



        lastX = d.x;
        lastY = d.y;

    }
    else{
        clearIt();
    }
}
function send(){
$.ajax({
    url:"http://192.168.43.1:5050/draw",
    type:"GET",
    success : function(data) {
        move(data);
    }
});

}

window.setInterval(send,200);