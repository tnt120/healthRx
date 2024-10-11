import { ImageType } from "../enums/image-type.enum";

export interface ImageResponse {
  imageType: ImageType;
  image: string;
}
