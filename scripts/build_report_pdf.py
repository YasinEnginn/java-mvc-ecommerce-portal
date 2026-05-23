from pathlib import Path
import argparse
import re
import sys

from reportlab.lib import colors
from reportlab.lib.pagesizes import A4
from reportlab.lib.styles import ParagraphStyle, getSampleStyleSheet
from reportlab.lib.units import cm
from reportlab.platypus import (
    ListFlowable,
    ListItem,
    Image,
    PageBreak,
    Paragraph,
    Preformatted,
    SimpleDocTemplate,
    Spacer,
    Table,
    TableStyle,
)
from reportlab.pdfbase.ttfonts import TTFont
from reportlab.pdfbase import pdfmetrics


ROOT = Path(__file__).resolve().parents[1]
REPORT_MD = ROOT / "docs" / "PROJE_RAPORU.md"
DEFAULT_OUTPUT = ROOT / "docs" / "ogrencino_rapor.pdf"


def register_fonts():
    candidates = [
        Path("C:/Windows/Fonts/arial.ttf"),
        Path("C:/Windows/Fonts/calibri.ttf"),
        Path("C:/Windows/Fonts/segoeui.ttf"),
    ]
    for candidate in candidates:
        if candidate.exists():
            pdfmetrics.registerFont(TTFont("ReportFont", str(candidate)))
            return "ReportFont"
    return "Helvetica"


def escape(text):
    text = text.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;")
    text = re.sub(r"`([^`]+)`", r"<font name='Courier'>\1</font>", text)
    text = text.replace("**", "")
    return text


def is_table_separator(line):
    return bool(re.match(r"^\|\s*:?-{3,}:?\s*(\|\s*:?-{3,}:?\s*)+\|?$", line.strip()))


def parse_table(lines, start):
    rows = []
    index = start
    while index < len(lines) and lines[index].strip().startswith("|"):
        if not is_table_separator(lines[index]):
            cells = [escape(cell.strip()) for cell in lines[index].strip().strip("|").split("|")]
            rows.append(cells)
        index += 1
    return rows, index


def apply_replacements(text, args):
    replacements = {
        "DOLDURULACAK_OGR_NO": args.student_no or "DOLDURULACAK_OGR_NO",
        "DOLDURULACAK_AD_SOYAD": args.name or "DOLDURULACAK_AD_SOYAD",
        "DOLDURULACAK_GITHUB_LINKI": args.github or "DOLDURULACAK_GITHUB_LINKI",
        "DOLDURULACAK_YOUTUBE_LINKI": args.youtube or "DOLDURULACAK_YOUTUBE_LINKI",
    }
    for old, new in replacements.items():
        text = text.replace(old, new)
    return text


def resolve_report_image(image_path):
    path = Path(image_path.strip())
    if not path.is_absolute():
        path = REPORT_MD.parent / path
    return path


def make_scaled_image(image_path, max_width, max_height):
    image = Image(str(image_path))
    scale = min(max_width / image.imageWidth, max_height / image.imageHeight, 1)
    image.drawWidth = image.imageWidth * scale
    image.drawHeight = image.imageHeight * scale
    return image


def image_cell(image_path, caption, styles, max_width, max_height):
    return [
        make_scaled_image(image_path, max_width, max_height),
        Paragraph(escape(caption), styles["ReportSmall"]),
    ]


def render_images(story, image_items, styles):
    if len(image_items) == 1:
        image_path, caption = image_items[0]
        if image_path.exists():
            story.append(make_scaled_image(image_path, 17.7 * cm, 10.6 * cm))
            if caption:
                story.append(Paragraph(escape(caption), styles["ReportSmall"]))
            story.append(Spacer(1, 5))
        else:
            story.append(Paragraph(escape(f"Görsel bulunamadı: {image_path}"), styles["ReportBody"]))
        return

    rows = []
    for index in range(0, len(image_items), 2):
        row = []
        for image_path, caption in image_items[index:index + 2]:
            if image_path.exists():
                row.append(image_cell(image_path, caption, styles, 8.55 * cm, 5.35 * cm))
            else:
                row.append([Paragraph(escape(f"Görsel bulunamadı: {image_path}"), styles["ReportSmall"])])
        if len(row) == 1:
            row.append("")
        rows.append(row)

    gallery = Table(rows, colWidths=[8.95 * cm, 8.95 * cm], hAlign="LEFT")
    gallery.setStyle(TableStyle([
        ("VALIGN", (0, 0), (-1, -1), "TOP"),
        ("LEFTPADDING", (0, 0), (-1, -1), 2),
        ("RIGHTPADDING", (0, 0), (-1, -1), 6),
        ("TOPPADDING", (0, 0), (-1, -1), 2),
        ("BOTTOMPADDING", (0, 0), (-1, -1), 7),
    ]))
    story.append(gallery)
    story.append(Spacer(1, 5))


def build_pdf(output_path, args):
    font = register_fonts()
    styles = getSampleStyleSheet()
    styles.add(ParagraphStyle(
        name="ReportTitle",
        parent=styles["Title"],
        fontName=font,
        fontSize=17,
        leading=20,
        spaceAfter=8,
    ))
    styles.add(ParagraphStyle(
        name="ReportHeading1",
        parent=styles["Heading1"],
        fontName=font,
        fontSize=12.5,
        leading=15,
        spaceBefore=7,
        spaceAfter=4,
        textColor=colors.HexColor("#123b5d"),
    ))
    styles.add(ParagraphStyle(
        name="ReportHeading2",
        parent=styles["Heading2"],
        fontName=font,
        fontSize=9.5,
        leading=12,
        spaceBefore=4,
        spaceAfter=3,
        textColor=colors.HexColor("#256f5b"),
    ))
    styles.add(ParagraphStyle(
        name="ReportBody",
        parent=styles["BodyText"],
        fontName=font,
        fontSize=8.3,
        leading=10.2,
        spaceAfter=2.5,
    ))
    styles.add(ParagraphStyle(
        name="ReportSmall",
        parent=styles["BodyText"],
        fontName=font,
        fontSize=6.3,
        leading=7.4,
    ))

    doc = SimpleDocTemplate(
        str(output_path),
        pagesize=A4,
        rightMargin=1.0 * cm,
        leftMargin=1.0 * cm,
        topMargin=1.0 * cm,
        bottomMargin=1.0 * cm,
        title="Web Programlama Final Projesi Raporu",
    )

    report_text = apply_replacements(REPORT_MD.read_text(encoding="utf-8"), args)
    lines = report_text.splitlines()
    story = []
    bullets = []
    in_code = False
    code_lines = []
    i = 0

    def flush_bullets():
        nonlocal bullets
        if bullets:
            story.append(ListFlowable(
                [ListItem(Paragraph(escape(item), styles["ReportBody"])) for item in bullets],
                bulletType="bullet",
                leftIndent=14,
            ))
            story.append(Spacer(1, 4))
            bullets = []

    while i < len(lines):
        line = lines[i]
        stripped = line.strip()

        if stripped.startswith("```"):
            if in_code:
                story.append(Preformatted("\n".join(code_lines), styles["Code"]))
                story.append(Spacer(1, 6))
                code_lines = []
                in_code = False
            else:
                flush_bullets()
                in_code = True
            i += 1
            continue

        if in_code:
            code_lines.append(line)
            i += 1
            continue

        if not stripped:
            flush_bullets()
            i += 1
            continue

        image_match = re.match(r"^!\[([^\]]*)\]\(([^)]+)\)$", stripped)
        if image_match:
            flush_bullets()
            image_items = []
            while i < len(lines):
                current = lines[i].strip()
                if not current:
                    i += 1
                    continue
                current_match = re.match(r"^!\[([^\]]*)\]\(([^)]+)\)$", current)
                if not current_match:
                    break
                image_items.append((
                    resolve_report_image(current_match.group(2)),
                    current_match.group(1).strip(),
                ))
                i += 1
            render_images(story, image_items, styles)
            continue

        if stripped.startswith("|"):
            flush_bullets()
            rows, i = parse_table(lines, i)
            if rows:
                table_data = [[Paragraph(cell, styles["ReportSmall"]) for cell in row] for row in rows]
                table = Table(table_data, repeatRows=1)
                table.setStyle(TableStyle([
                    ("BACKGROUND", (0, 0), (-1, 0), colors.HexColor("#e8edf2")),
                    ("TEXTCOLOR", (0, 0), (-1, 0), colors.HexColor("#1f2937")),
                    ("GRID", (0, 0), (-1, -1), 0.25, colors.HexColor("#b9c2cf")),
                    ("VALIGN", (0, 0), (-1, -1), "TOP"),
                    ("FONTNAME", (0, 0), (-1, -1), font),
                    ("LEFTPADDING", (0, 0), (-1, -1), 3),
                    ("RIGHTPADDING", (0, 0), (-1, -1), 3),
                    ("TOPPADDING", (0, 0), (-1, -1), 2),
                    ("BOTTOMPADDING", (0, 0), (-1, -1), 2),
                ]))
                story.append(table)
                story.append(Spacer(1, 4))
            continue

        if stripped.startswith("# "):
            flush_bullets()
            story.append(Paragraph(escape(stripped[2:]), styles["ReportTitle"]))
        elif stripped.startswith("## "):
            flush_bullets()
            if story:
                story.append(Spacer(1, 2))
            story.append(Paragraph(escape(stripped[3:]), styles["ReportHeading1"]))
        elif stripped.startswith("### "):
            flush_bullets()
            story.append(Paragraph(escape(stripped[4:]), styles["ReportHeading2"]))
        elif stripped.startswith("- "):
            bullets.append(stripped[2:])
        elif re.match(r"^\d+\.\s+", stripped):
            flush_bullets()
            story.append(Paragraph(escape(stripped), styles["ReportBody"]))
        else:
            flush_bullets()
            story.append(Paragraph(escape(stripped), styles["ReportBody"]))
        i += 1

    flush_bullets()
    doc.build(story)


if __name__ == "__main__":
    parser = argparse.ArgumentParser(description="Proje raporunu PDF olarak uretir.")
    parser.add_argument("output", nargs="?", default=str(DEFAULT_OUTPUT), help="PDF cikti yolu")
    parser.add_argument("--student-no", help="Ogrenci numarasi")
    parser.add_argument("--name", help="Ad soyad")
    parser.add_argument("--github", help="GitHub kaynak kod linki")
    parser.add_argument("--youtube", help="YouTube video linki")
    parsed = parser.parse_args()

    output = Path(parsed.output)
    output.parent.mkdir(parents=True, exist_ok=True)
    build_pdf(output, parsed)
    print(output)
