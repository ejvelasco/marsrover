import * as React from 'react';
import { makeStyles } from '@material-ui/core/styles';
import Card from '@material-ui/core/Card';
import CardActionArea from '@material-ui/core/CardActionArea';
import CardContent from '@material-ui/core/CardContent';
import CardMedia from '@material-ui/core/CardMedia';
import Typography from '@material-ui/core/Typography';

const trollImageUrl =
  'https://images-na.ssl-images-amazon.com/images/I/51w7koDjFsL._AC_.jpg';
const badDate = 'April 31, 2018';
const trollMessage = 'Cute';

export const RoverImageCard = ({ date, rover, maxWidth }) => {
  const useStyles = makeStyles({
    root: {
      maxWidth,
    },
  });
  const classes = useStyles();
  const capitalize = (s: string) => {
    const first = s.charAt(0).toUpperCase();
    return first + s.slice(1);
  };
  const capitalRover = capitalize(rover);
  const imageEndpoint = `api/rovers/${rover}/images`;

  return (
    <Card className={classes.root}>
      <CardActionArea>
        <CardMedia
          component="img"
          alt={`Image from ${date}`}
          height="140"
          image={
            date === badDate
              ? trollImageUrl
              : `${imageEndpoint}?date=${encodeURIComponent(date)}`
          }
          title={date}
        />
        <CardContent>
          <Typography gutterBottom variant="h5" component="h2">
            {date}
          </Typography>
          <Typography variant="body2" color="textSecondary" component="p">
            {date === badDate ? trollMessage : capitalRover}
          </Typography>
        </CardContent>
      </CardActionArea>
    </Card>
  );
};
